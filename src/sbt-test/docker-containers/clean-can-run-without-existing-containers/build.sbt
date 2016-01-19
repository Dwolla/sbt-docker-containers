name := "DockerContainerPlugin-clean-can-run-without-existing-containers"

version := "1.0"

scalaVersion := "2.11.6"

val dockerContainersPlugin = (project in file("."))
  .enablePlugins(DockerContainerPlugin)
