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
//    "ch.qos.logback" % "logback-classic" % "1.0.0" % "runtime" ::
    "com.typesafe.akka" %% "akka-actor" % akka ::
    "com.typesafe.akka" %% "akka-slf4j" % akka ::
    "io.spray" %% "spray-caching" % spray ::
    "io.spray" %% "spray-can" % spray ::
    "io.spray" %% "spray-routing" % spray ::
    "io.spray" %% "spray-json" % "1.3.1" ::
    "com.typesafe.slick" %% "slick" % "3.0.0" ::
    "org.postgresql" % "postgresql" % "9.4-1201-jdbc4" ::
    "com.zaxxer" % "HikariCP" % "2.3.5" ::
    Nil

scalariformSettings

//javaOptions := Seq("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
    val _ = initialize.value
    if (sys.props("java.specification.version") != "1.8")
        sys.error("Java 8 is required for this project.")
}

seq(Revolver.settings: _*)
