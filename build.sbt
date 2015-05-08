name := "ENGINE"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
//resolvers += "Scalaz Bintray Repo"    at "http://dl.bintray.com/scalaz/releases"

val akka = "2.3.10"
val spray = "1.3.3"

libraryDependencies ++=
    "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime" ::
    "com.typesafe.akka" %% "akka-actor" % akka ::
    "com.typesafe.akka" %% "akka-slf4j" % akka ::
    "io.spray" %% "spray-caching" % spray ::
    "io.spray" %% "spray-can" % spray ::
    "io.spray" %% "spray-routing" % spray ::
    "io.spray" %% "spray-json" % "1.3.1" ::
    "com.typesafe.slick" %% "slick" % "2.1.0" ::
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc4" ::
    "com.mchange" % "c3p0" % "0.9.5" ::
    Nil

scalariformSettings

javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005")

seq(Revolver.settings: _*)
