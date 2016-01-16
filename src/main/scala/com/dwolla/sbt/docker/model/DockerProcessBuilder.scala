package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerContainerPlugin
import sbt.ProcessBuilder

trait DockerProcessBuilder {
  def toDockerProcessBuilder: ProcessBuilder = DockerContainerPlugin.dockerProcess(toSeq: _*)

  def toSeq: Seq[String]
}
