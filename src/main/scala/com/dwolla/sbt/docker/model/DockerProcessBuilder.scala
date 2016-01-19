package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import sbt.{Process, ProcessBuilder}

trait DockerProcessBuilder {
  def toDockerProcessBuilder: ProcessBuilder = Process(dockerCommand, argumentSequence)

  def argumentSequence: Seq[String]
}
