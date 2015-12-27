package com.dwolla.sbt.docker

import sbt.TaskKey
import sbt.settingKey

trait DockerContainerKeys {
  lazy val createLocal = TaskKey[String]("createLocal", "Use the newly-built image and create a local container with the right parameters")
  lazy val runLocal = TaskKey[Unit]("runLocal", "Build and publish the docker container, then start it")

  lazy val dockerContainerName = settingKey[String]("Name of the container to be created. Defaults to project name")
  lazy val dockerContainerMemoryLimit = settingKey[Option[String]]("memory limit for created Docker container. e.g., Option('192M')")

  lazy val dockerContainerPortForwarding = settingKey[Map[Int, Option[Int]]]("Docker container:host port mappings")
  lazy val dockerContainerAutoForwardAllPorts = settingKey[Boolean]("Auto-map all exposed ports")
}
