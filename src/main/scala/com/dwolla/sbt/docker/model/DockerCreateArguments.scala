package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import reified.DockerCreateReifiedCommandLineArguments

case class DockerCreateArguments(containerName: String,
                                 imageName: String,
                                 memoryLimit: Option[String],
                                 publishedPorts: Map[Int, Option[Int]],
                                 autoPublishAllPorts: Boolean,
                                 linkedContainers: Map[String, String])
  extends DockerProcessReifiedCommandLineArgumentBuilder[DockerCreateReifiedCommandLineArguments] {

  def toDockerProcessReifiedCommandLineArguments = DockerCreateReifiedCommandLineArguments(
    setContainerName(containerName),
    imageName,
    memoryLimit.map(memoryLimitToDockerCommand),
    portMappings(publishedPorts),
    publishAllExposedPorts(autoPublishAllPorts),
    links(linkedContainers)
  )

  private def publishAllExposedPorts(enabled: Boolean): Option[String] = if (enabled) Some(publishAllPorts) else None

  private def memoryLimitToDockerCommand(s: String): String = s"$memory $s"

  private def portMappings(mappings: Map[Int, Option[Int]]) = mappings.map {
    case (container, maybeHost) ⇒
      val host = maybeHost.map(host ⇒ s":$host").getOrElse("")
      s"$publishPort $container$host"
  }.toSet

  private def links(mappings: Map[String, String]) = mappings.map {
    case (name, alias) ⇒ s"$link $name:$alias"
  }.toSet

  private def setContainerName(containerName: String): String = s"$containerNameDockerOption $containerName"
}
