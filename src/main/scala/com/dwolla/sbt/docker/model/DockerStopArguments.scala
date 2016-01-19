package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._

case class DockerStopArguments(containerName: String) extends DockerProcessBuilder {
  override def argumentSequence = Seq(dockerStop, containerName)
}
