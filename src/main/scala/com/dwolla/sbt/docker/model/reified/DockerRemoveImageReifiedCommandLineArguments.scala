package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import com.dwolla.sbt.docker.model.DockerProcessBuilder

case class DockerRemoveImageReifiedCommandLineArguments(imageNames: Set[String]) extends DockerProcessBuilder {
  override def toSeq: Seq[String] = removeImage +: imageNames.toSeq
}
