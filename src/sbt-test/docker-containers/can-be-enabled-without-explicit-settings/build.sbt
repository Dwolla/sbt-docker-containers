name := "DockerContainerPlugin-can-be-enabled-without-explicitly-setting-any-of-its-keys"

version := "1.0"

scalaVersion := "2.11.6"

val dockerContainersPlugin = (project in file("."))
  .enablePlugins(DockerContainerPlugin)
