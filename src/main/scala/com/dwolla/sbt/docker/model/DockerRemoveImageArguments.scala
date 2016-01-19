package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._

case class DockerRemoveImageArguments(imageNames: String*) extends DockerProcessBuilder {
  override def argumentSequence = removeImage +: imageNames.distinct
}
