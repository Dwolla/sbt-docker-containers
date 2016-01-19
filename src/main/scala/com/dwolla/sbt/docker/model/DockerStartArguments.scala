package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._

case class DockerStartArguments(containerName: String) extends DockerProcessBuilder {
  override def argumentSequence: Seq[String] = Seq(dockerStart, containerName)
}
