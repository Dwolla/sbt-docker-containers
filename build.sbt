import java.lang.System._

lazy val buildVersion = {
  val mainVersion = "1.1"
  val minorVersion = Option(getenv("TRAVIS_BUILD_NUMBER"))
  minorVersion match {
    case Some(v: String) ⇒ s"$mainVersion.$v"
    case None ⇒ mainVersion + "-SNAPSHOT"
  }
}

lazy val specs2Version = "3.6.6"

lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "docker-containers",
  homepage := Some(url("https://github.com/Dwolla/sbt-docker-containers")),
  description := "SBT plugin to define and manage Docker containers based on images creating using sbt-native-packager",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  version := buildVersion,
  scalaVersion := "2.10.6",
  sbtPlugin := true,
  startYear := Option(2016),
  libraryDependencies ++= Seq(
    "org.specs2"     %% "specs2-core"     % specs2Version  % "test",
    "org.specs2"     %% "specs2-mock"     % specs2Version  % "test"
  )
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/sbt-docker-containers"),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")

lazy val pipeline = InputKey[Unit]("pipeline", "Runs the full build pipeline: compile, test, integration tests")
pipeline := scripted.dependsOn(test in Test).evaluated

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

// uncomment to see sbt output for each scripted test run
//scriptedBufferLog := false

val dockerContainersPlugin = (project in file("."))
  .settings(buildSettings ++ bintraySettings: _*)
  .settings(ScriptedPlugin.scriptedSettings: _*)
