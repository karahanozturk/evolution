import play.PlayScala

name := """evolution"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.4"

lazy val root = Project ("evolution", file (".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
)
