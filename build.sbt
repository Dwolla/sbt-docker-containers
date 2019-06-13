lazy val buildSettings = Seq(
  organization := "com.dwolla.sbt",
  name := "docker-containers",
  homepage := Some(url("https://github.com/Dwolla/sbt-docker-containers")),
  description := "SBT plugin to define and manage Docker containers based on images creating using sbt-native-packager",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  crossSbtVersions := Vector("1.2.8"),
  sbtPlugin := true,
  startYear := Option(2016),
  addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.22"),
  libraryDependencies ++= {
    val specs2Version = "4.5.1"

    Seq(
      "org.specs2" %% "specs2-core" % specs2Version % Test,
    )
  },
  releaseVersionBump := sbtrelease.Version.Bump.Minor,
  releaseProcess --= {
    import ReleaseTransformations._
    Seq(runClean, runTest, publishArtifacts)
  }
)

lazy val bintraySettings = Seq(
  bintrayVcsUrl := Some("https://github.com/Dwolla/sbt-docker-containers"),
  publishMavenStyle := false,
  bintrayRepository := "sbt-plugins",
  bintrayOrganization := Option("dwolla"),
  pomIncludeRepository := { _ ⇒ false }
)

lazy val pipeline = InputKey[Unit]("pipeline", "Runs the full build pipeline: compile, test, integration tests")
pipeline := scripted.dependsOn(test in Test).evaluated

val dockerContainersPlugin = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(buildSettings ++ bintraySettings: _*)
