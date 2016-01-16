package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions.dockerStop
import com.dwolla.sbt.docker.model.DockerProcessBuilder

case class DockerStopReifiedCommandLineArguments(containerName: String) extends DockerProcessBuilder {
  override def toSeq: Seq[String] = Seq(dockerStop, containerName)
}
