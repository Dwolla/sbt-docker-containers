package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import sbt.TaskKey
import sbt.settingKey
import DockerPlugin.autoImport._

trait DockerContainerKeys {
  lazy val createLocalDockerContainer = TaskKey[String]("createLocal", "Use the newly-built image and create a local container with the right parameters") in Docker
  lazy val runLocalDockerContainer = TaskKey[Unit]("runLocal", "Build and publish the docker container, then start it") in Docker

  lazy val dockerContainerMemoryLimit = settingKey[Option[String]]("memory limit for created Docker container. e.g., Option('192M')")

  lazy val dockerContainerPortForwarding = settingKey[Map[Int, Option[Int]]]("Docker container:host port mappings")
  lazy val dockerContainerAutoForwardAllPorts = settingKey[Boolean]("Auto-map all exposed ports")
}
