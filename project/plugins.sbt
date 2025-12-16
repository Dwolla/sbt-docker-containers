logLevel := Level.Warn

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("org.typelevel" % "sbt-typelevel-ci-release" % "0.8.4")
addSbtPlugin("org.typelevel" % "sbt-typelevel-settings" % "0.8.4")
addSbtPlugin("org.typelevel" % "sbt-typelevel-mergify" % "0.8.4")
