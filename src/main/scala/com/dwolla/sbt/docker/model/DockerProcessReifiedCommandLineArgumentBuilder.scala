package com.dwolla.sbt.docker.model

trait DockerProcessReifiedCommandLineArgumentBuilder[T <: DockerProcessBuilder] {
  def toDockerProcessReifiedCommandLineArguments: T
}
