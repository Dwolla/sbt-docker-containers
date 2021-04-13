logLevel := Level.Warn

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.5")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.17")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.10.1")
