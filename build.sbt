import org.typelevel.sbt.TypelevelMimaPlugin.autoImport

ThisBuild / organization := "com.dwolla.sbt"
ThisBuild / startYear := Option(2016)
ThisBuild / homepage := Option(url("https://github.com/Dwolla/sbt-docker-containers"))
ThisBuild / licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
ThisBuild / developers := List(
  Developer(
    "bpholt",
    "Brian Holt",
    "bholt@dwolla.com",
    url("https://dwolla.com")
  )
)
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("11"), JavaSpec.temurin("17"))
ThisBuild / tlJdkRelease := Option(8)
ThisBuild / githubWorkflowScalaVersions := Seq("2.12")
ThisBuild / tlCiReleaseBranches := Seq("main")
ThisBuild / githubWorkflowBuild ~= { steps =>
  WorkflowStep.Sbt(List("test", "scripted"), name = Some("Test")) :: steps.filterNot(_.name.contains("Test")).toList
}
ThisBuild / tlBaseVersion := "1.6"
ThisBuild / tlVersionIntroduced:= Map("2.12" -> "1.2")
ThisBuild / mergifyStewardConfig ~= { _.map {
  _.withAuthor("dwolla-oss-scala-steward[bot]")
    .withMergeMinors(true)
}}

lazy val `docker-containers` = (project in file("."))
  .settings(
    sbtPlugin := true,
    description := "SBT plugin to define and manage Docker containers based on images creating using sbt-native-packager",
    addSbtPlugin("com.github.sbt" %% "sbt-native-packager" % "1.11.6"),
    libraryDependencies ++= {
      val specs2Version = "4.20.5"

      Seq(
        "org.specs2" %% "specs2-core" % specs2Version % Test,
      )
    },
  )
  .enablePlugins(SbtPlugin)
