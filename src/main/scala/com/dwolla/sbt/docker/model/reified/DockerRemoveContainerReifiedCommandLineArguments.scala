package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions.removeContainer
import com.dwolla.sbt.docker.model.DockerProcessBuilder

case class DockerRemoveContainerReifiedCommandLineArguments(containerName: String, volumes: Option[String]) extends DockerProcessBuilder {
  override def toSeq: Seq[String] = (removeContainer :: volumes.toList) :+ containerName
}
