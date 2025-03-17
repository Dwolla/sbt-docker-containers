logLevel := Level.Warn

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.7.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.7.7")
addSbtPlugin("org.typelevel" % "sbt-typelevel-mergify" % "0.7.7")
