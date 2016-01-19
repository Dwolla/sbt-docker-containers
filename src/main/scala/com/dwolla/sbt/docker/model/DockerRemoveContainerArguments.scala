package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions
import com.dwolla.sbt.docker.DockerCommandLineOptions._

case class DockerRemoveContainerArguments(containerName: String, volumes: Boolean = false) extends DockerProcessBuilder {
  override def argumentSequence: Seq[String] = Seq(removeContainer, s"${DockerCommandLineOptions.volumes}=$volumes", containerName)
}
