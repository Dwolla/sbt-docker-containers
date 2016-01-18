package com.dwolla.sbt.docker.model

import reified.DockerStopReifiedCommandLineArguments

case class DockerStopArguments(containerName: String) extends DockerProcessReifiedCommandLineArgumentBuilder[DockerStopReifiedCommandLineArguments] {
  override def toDockerProcessReifiedCommandLineArguments = DockerStopReifiedCommandLineArguments(containerName)
}
