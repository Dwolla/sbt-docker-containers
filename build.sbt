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
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("8"), JavaSpec.temurin("11"))
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches :=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted"), name = Some("Build and test project")))
ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))
ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

lazy val `docker-containers` = (project in file("."))
  .settings(
    sbtPlugin := true,
    description := "SBT plugin to define and manage Docker containers based on images creating using sbt-native-packager",
    sonatypeProfileName := "com.dwolla",
    addSbtPlugin("com.github.sbt" %% "sbt-native-packager" % "1.9.13"),
    libraryDependencies ++= {
      val specs2Version = "4.19.0"

      Seq(
        "org.specs2" %% "specs2-core" % specs2Version % Test,
      )
    },
  )
  .enablePlugins(SbtPlugin)
