package com.dwolla.sbt.docker

object DockerCommandLineOptions {
  val dockerCommand = "docker"
  val dockerStart = "start"
  val dockerStop = "stop"
  val dockerCreate = "create"
  val removeContainer = "rm"
  val removeImage = "rmi"

  val publishPort = "--publish"
  val containerName = "--name"
  val link = "--link"
  val publishAllPorts = "--publish-all"
  val memory = "--memory"
  val volumes = "--volumes"
  val environment = "--env"
  val network = "--network"
}
