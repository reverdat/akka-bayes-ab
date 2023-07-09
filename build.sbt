ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.7"

lazy val root = (project in file("."))
  .settings(
    name := "akka-bayes-ab"
  )

val akkaVersion = "2.5.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.stripe" %% "rainier-core" % "0.3.5",
  "org.scalatest" %% "scalatest" % "3.0.5"
)

