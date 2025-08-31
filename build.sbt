ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "chatops4s-slack",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.30",
      "org.http4s" %% "http4s-ember-client" % "0.23.30",
      "org.http4s" %% "http4s-dsl" % "0.23.30",
      "org.http4s" %% "http4s-circe" % "0.23.30",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.11.33",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.11.33",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.11.33",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.11.33",
      "io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "org.slf4j" % "slf4j-simple" % "2.0.16",
      "com.softwaremill.sttp.client3" %% "core" % "3.10.1",
      "com.softwaremill.sttp.client3" %% "circe" % "3.10.1",
      "com.softwaremill.sttp.client3" %% "http4s-backend" % "3.10.1",
      "com.github.pureconfig" %% "pureconfig-core" % "0.17.9",
      "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.9",
    )
  )

