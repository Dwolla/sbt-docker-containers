package com.dwolla.sbt.docker

object DockerCommandLineOptions {
  val publishPort = "-publish"
  def publishAllExposedPorts(enabled: Boolean): String = if (enabled) "--publish-all" else ""

  def memoryLimitToDockerCommand(s: String): String = s"--memory $s"
  def portMappingToDockerCommand(tuple: (Int, Option[Int])): String = tuple match {
    case (container: Int, maybeHost: Option[Int]) ⇒
      val host = maybeHost.map(host ⇒ s":$host").getOrElse("")
      s"$publishPort $container$host"
  }

  def portMappings(mappings: Map[Int, Option[Int]], autoForwardPorts: Boolean) =
    (mappings.map(portMappingToDockerCommand).toList :+ publishAllExposedPorts(autoForwardPorts)).mkString(" ")
}
