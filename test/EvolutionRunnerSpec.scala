import java.sql.DriverManager

import anorm._
import org.scalatest.{FlatSpec, Matchers}
import play.api.db.DB
import play.api.test.FakeApplication
import util.EvolutionRunner

class EvolutionRunnerSpec extends FlatSpec with Matchers {

  "Evolution Runner" should "run the evolution sql scripts with Play App" in {
    implicit val app = FakeApplication(
      additionalConfiguration = Map("db.default.url" -> "jdbc:h2:mem:play;DB_CLOSE_DELAY=-1",
        "db.default.driver" -> "org.h2.Driver",
        "db.default.user" -> ""))
    EvolutionRunner.setup()

    DB.withConnection { implicit conn =>
      SQL("insert into test.users(name) values ({name})")
        .on("name" -> "kara")
        .executeInsert()
    }

    DB.withConnection { implicit conn =>
      val users = SQL("select * from test.users").apply()
      users.head[String]("name") should be("kara")
    }

    EvolutionRunner.tearDown()
  }

  "Evolution Runner" should "run the evolution sql scripts without any Play App" in {
    EvolutionRunner.setupWithoutApp()

    Class.forName("org.h2.Driver")
    implicit val conn = DriverManager.getConnection("jdbc:h2:mem:play;DB_CLOSE_DELAY=-1", "", "")

    SQL("insert into test.users(name) values ({name})")
      .on("name" -> "oz")
      .executeInsert()

    val users = SQL("select * from test.users").apply()
    users.head[String]("name") should be("oz")

    EvolutionRunner.tearDownWithoutApp()
  }
}