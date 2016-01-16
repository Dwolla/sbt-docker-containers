package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions.dockerStart
import com.dwolla.sbt.docker.model.DockerProcessBuilder

case class DockerStartReifiedCommandLineArguments(containerName: String) extends DockerProcessBuilder {
  override def toSeq: Seq[String] = Seq(dockerStart, containerName)
}
