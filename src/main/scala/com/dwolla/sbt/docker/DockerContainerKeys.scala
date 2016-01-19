package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import DockerPlugin.autoImport._
import sbt.{TaskKey, settingKey}

trait DockerContainerKeys {
  lazy val createLocalDockerContainer = TaskKey[String]("createLocal", "Use the newly-built image and create a local container with the right parameters") in Docker
  lazy val startLocalDockerContainer = TaskKey[Unit]("startLocal", "Start the Docker container. Depends on createLocal to create the container to be started.") in Docker

  lazy val dockerContainerMemoryLimit = settingKey[Option[String]]("memory limit for created Docker container. e.g., Option('192M')")

  lazy val dockerContainerPortPublishing = settingKey[Map[Int, Option[Int]]]("Docker container:host port mappings")
  lazy val dockerContainerPublishAllPorts = settingKey[Boolean]("Auto-map all exposed ports")

  lazy val dockerContainerLinks = settingKey[Map[String, String]]("Linked Docker containers")
  lazy val dockerContainerAdditionalEnvironmentVariables = settingKey[Map[String, Option[String]]]("additional environment variables to be set on created container")

  lazy val AutoAssign = None
  lazy val Passthru = None

  @deprecated("runLocal is the wrong term because this task specifically starts an already-created container", "1.1")
  lazy val runLocalDockerContainer = TaskKey[Unit]("runLocal", "Deprecated. Use 'startLocal' instead.") in Docker
}
