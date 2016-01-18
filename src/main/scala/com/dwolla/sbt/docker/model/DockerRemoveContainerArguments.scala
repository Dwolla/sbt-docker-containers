package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions
import reified.DockerRemoveContainerReifiedCommandLineArguments

case class DockerRemoveContainerArguments(containerName: String, volumes: Boolean = false)
  extends DockerProcessReifiedCommandLineArgumentBuilder[DockerRemoveContainerReifiedCommandLineArguments] {
  override def toDockerProcessReifiedCommandLineArguments =
    DockerRemoveContainerReifiedCommandLineArguments(containerName, Option(s"${DockerCommandLineOptions.volumes}=$volumes"))
}
