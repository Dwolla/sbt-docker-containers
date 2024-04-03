logLevel := Level.Warn

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.11")
addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.1")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.14.2")
