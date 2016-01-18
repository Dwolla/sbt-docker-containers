package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.model.DockerProcessBuilder

case class DockerCreateReifiedCommandLineArguments(containerName: String,
                                                   imageName: String,
                                                   memoryLimit: Option[String],
                                                   publishedPorts: Set[String],
                                                   autoPublishAllPorts: Option[String],
                                                   linkedContainers: Set[String],
                                                   environment: Set[String]
                                                  ) extends DockerProcessBuilder {
  override def toSeq: Seq[String] = Seq(
    "create",
    containerName,
    memoryLimit.getOrElse(""),
    autoPublishAllPorts.getOrElse("")
  ) ++
    publishedPorts ++
    linkedContainers ++
    environment :+
    imageName
}
