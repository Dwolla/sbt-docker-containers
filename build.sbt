inThisBuild(List(
  organization := "com.dwolla.sbt",
  sonatypeProfileName := "com.dwolla",
  description := "SBT plugin to define and manage Docker containers based on images creating using sbt-native-packager",
  sbtPlugin := true,
  startYear := Option(2016),
  addSbtPlugin("com.typesafe.sbt" %% "sbt-native-packager" % "1.3.22"),
  libraryDependencies ++= {
    val specs2Version = "4.5.1"

    Seq(
      "org.specs2" %% "specs2-core" % specs2Version % Test,
    )
  },
  homepage := Option(url("https://github.com/Dwolla/sbt-docker-containers")),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  developers := List(
    Developer(
      "bpholt",
      "Brian Holt",
      "bholt@dwolla.com",
      url("https://dwolla.com")
    )
  ),
  githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11"),
  githubWorkflowTargetTags ++= Seq("v*"),
  githubWorkflowPublishTargetBranches :=
    Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
  githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted"), name = Some("Build and test project"))),
  githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release"))),
  githubWorkflowPublish := Seq(
    WorkflowStep.Sbt(
      List("ci-release"),
      env = Map(
        "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
        "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
        "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
        "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
      )
    )
  ),
))

val `docker-containers` = (project in file("."))
  .enablePlugins(SbtPlugin)
