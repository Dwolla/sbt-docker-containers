package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions
import com.dwolla.sbt.docker.DockerCommandLineOptions._
import reified.DockerCreateReifiedCommandLineArguments

case class DockerCreateArguments(containerName: String,
                                 imageName: String,
                                 memoryLimit: Option[String],
                                 publishedPorts: Map[Int, Option[Int]],
                                 autoPublishAllPorts: Boolean,
                                 linkedContainers: Map[String, String],
                                 environment: Map[String, Option[String]])
  extends DockerProcessReifiedCommandLineArgumentBuilder[DockerCreateReifiedCommandLineArguments] {

  def toDockerProcessReifiedCommandLineArguments = DockerCreateReifiedCommandLineArguments(
    setContainerName(containerName),
    imageName,
    memoryLimit.map(memoryLimitToDockerCommand),
    portMappings,
    publishAllExposedPorts(autoPublishAllPorts),
    links,
    transformedEnvironment
  )

  private def publishAllExposedPorts(enabled: Boolean): Option[String] = if (enabled) Some(publishAllPorts) else None

  private def memoryLimitToDockerCommand(s: String): String = s"$memory $s"

  private def portMappings = publishedPorts.map {
    case (container, maybeHost) ⇒
      val host = maybeHost.map(host ⇒ s":$host").getOrElse("")
      s"$publishPort $container$host"
  }.toSet

  private def links = linkedContainers.map {
    case (name, alias) ⇒ s"$link $name:$alias"
  }.toSet

  private def setContainerName(containerName: String): String = s"$containerNameDockerOption $containerName"

  private def transformedEnvironment: Set[String] = environment.map {
    case (name, value) ⇒ s"${DockerCommandLineOptions.environment} ${(name :: value.toList).mkString("=")}"
  }.toSet
}
