logLevel := Level.Warn

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.3.1")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.14.2")
