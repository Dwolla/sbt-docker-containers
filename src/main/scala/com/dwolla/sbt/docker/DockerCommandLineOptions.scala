package com.dwolla.sbt.docker

object DockerCommandLineOptions {
  val dockerCommand = "docker"
  val dockerStart = "start"
  val dockerStop = "stop"
  val removeContainer = "rm"
  val removeImage = "rmi"

  val publishPort = "--publish"
  val containerNameDockerOption = "--name"
  val link = "--link"
  val publishAllPorts = "--publish-all"
  val memory = "--memory"
  val volumes = "--volumes"
}
