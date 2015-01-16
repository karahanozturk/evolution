package util

import java.io.File
import java.sql.{Connection, DriverManager}

import org.apache.commons.io.FileUtils
import play.api.Application
import play.api.db.DB

import scala.collection.JavaConverters._

object EvolutionRunner {

  val defaultPath = "conf/evolutions/default/"

  def setup(sqlFilesPath: String = defaultPath)(implicit app: Application) {
    runWithApp(sqlFilesPath, e => e.upQueries)
  }

  def tearDown(sqlFilesPath: String = defaultPath)(implicit app: Application) {
    runWithApp(sqlFilesPath, e => e.downQueries)
  }

  def setupWithoutApp(sqlFilesPath: String = defaultPath) {
    runWithoutApp(sqlFilesPath, e => e.upQueries)
  }

  def tearDownWithoutApp(sqlFilesPath: String = defaultPath) {
    runWithoutApp(sqlFilesPath, e => e.downQueries)
  }

  private def getEvolutions(directory: File) = {
    val evolutionFiles = FileUtils.listFiles(directory, Array("sql"), false).asScala
    evolutionFiles.map { evolution =>
      val upsDowns = FileUtils.
        readFileToString(evolution).
        split("# --- !Ups")(1).
        split("# --- !Downs")
      val index = evolution.getName.replace(".sql", "").toInt
      new Evolution(index, upsDowns(0), upsDowns(1))
    }
  }

  private def runQueries(queries: Array[String])(implicit conn: Connection) {
    queries.foreach {
      conn.createStatement.execute
    }
  }

  private def runWithApp(sqlFilesPath: String, getQueries: Evolution => Array[String])(implicit app: Application) = {
    def runQueriesInTransaction(e: Evolution)(implicit app: Application) {
      DB.withTransaction { implicit conn =>
        runQueries(getQueries(e))
      }
    }

    getEvolutions(app.getFile(sqlFilesPath)).foreach(runQueriesInTransaction)
  }

  private def runWithoutApp(sqlFilesPath: String, getQueries: Evolution => Array[String]) {
    Class.forName("org.h2.Driver")
    implicit val conn = DriverManager.getConnection("jdbc:h2:mem:play;DB_CLOSE_DELAY=-1", "", "")
    getEvolutions(new File(sqlFilesPath)).foreach(e => runQueries(getQueries(e)))
  }
}

private case class Evolution(index: Int, up: String, down: String) {
  val upQueries = up.trim.split(";")
  val downQueries = down.trim.split(";")
}