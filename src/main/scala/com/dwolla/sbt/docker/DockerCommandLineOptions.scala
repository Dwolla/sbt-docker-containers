package com.dwolla.sbt.docker

object DockerCommandLineOptions {
  val dockerCommand = "docker"
  val dockerStart = "start"

  val publishPort = "--publish"
  val containerNameDockerOption = "--name"
  val link = "--link"
  val publishAllPorts = "--publish-all"
  val memory = "--memory"
}
