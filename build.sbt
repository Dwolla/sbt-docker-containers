import java.lang.System._

lazy val artifactoryBase = "http://artifactory.dwolla.net:8081/artifactory"

lazy val buildVersion = {
  val mainVersion = "1.1"
  val minorVersion = Option(getenv("BUILD_NUMBER"))
  val verificationSuffix = Option(getenv("JOB_NAME")).filterNot(_.toLowerCase.endsWith("_publish")).map(name ⇒ s"-$name").getOrElse("")
  minorVersion match {
    case Some(v: String) ⇒ s"$mainVersion.$v$verificationSuffix"
    case None ⇒ mainVersion + "-SNAPSHOT"
  }
}

lazy val specs2Version = "3.6.6"

lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "docker-containers",
  homepage := Some(url("https://stash.dwolla.net/projects/SUP/repos/sbt-docker-containers/browse")),
  version := buildVersion,
  scalaVersion := "2.10.6",
  sbtPlugin := true,
  resolvers += "artifactory" at s"$artifactoryBase/repo",
  libraryDependencies ++= Seq(
    "org.specs2"     %% "specs2-core"     % specs2Version  % "test",
    "org.specs2"     %% "specs2-mock"     % specs2Version  % "test",
    "org.mockito"    %  "mockito-all"     % "1.9.5"        % "test"
  )
)

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.6")

lazy val publishSettings = Seq(
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  publishArtifact in(Compile, packageBin) := true,
  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in(Compile, packageSrc) := true,
  publishTo := {
    if (buildVersion.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at s"$artifactoryBase/libs-snapshot-local")
    else
      Some("releases" at s"$artifactoryBase/libs-release-local")
  }
)

lazy val pipeline = InputKey[Unit]("pipeline", "Runs the full build pipeline: compile, test, integration tests")
pipeline := scripted.dependsOn(test in Test).evaluated

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}

// uncomment to see sbt output for each scripted test run
//scriptedBufferLog := false

val dockerContainersPlugin = (project in file("."))
  .settings(buildSettings: _*)
  .settings(publishSettings: _*)
  .settings(ScriptedPlugin.scriptedSettings: _*)
