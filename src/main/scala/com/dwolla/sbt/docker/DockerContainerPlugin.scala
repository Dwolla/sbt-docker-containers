package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import sbt.Keys._
import sbt._

import scala.language.postfixOps

object DockerContainerPlugin extends AutoPlugin {

  object autoImport extends DockerContainerKeys

  import DockerPlugin.autoImport._
  import autoImport._
  import DockerCommandLineOptions._

  override def requires = DockerPlugin

  lazy val defaultValues = Seq(
    dockerContainerName := name.value,
    (version in createLocal) := version.value,
    dockerContainerMemoryLimit := None,
    dockerContainerAutoForwardAllPorts := false,
    dockerContainerPortForwarding := Map.empty[Int, Option[Int]]
  )

  lazy val tasks = Seq(
    createLocal <<= (
      dockerContainerMemoryLimit,
      dockerContainerPortForwarding,
      dockerContainerAutoForwardAllPorts,
      dockerTarget in Docker,
      dockerContainerName,
      publishLocal
      ) map { (
                optionalMemoryLimit,
                portForwardMappings,
                autoForwardPorts,
                imageName,
                containerName, _) ⇒

      val memoryLimit = optionalMemoryLimit.map(memoryLimitToDockerCommand).getOrElse("")
      val portForwarding = portMappings(portForwardMappings, autoForwardPorts)

      s"docker create --name $containerName $portForwarding $memoryLimit $imageName" !

      containerName
    },
    runLocal <<= createLocal map { containerName ⇒
      s"docker start $containerName" !
    },
    clean <<= (dockerContainerName, dockerTarget in Docker, clean) map { (containerName, imageName, _) ⇒
      s"docker stop $containerName" !;
      s"docker rm $containerName" !;
      s"docker rmi $imageName" !
    })

  lazy val baseDockerContainerSettings = defaultValues ++ tasks

  override lazy val projectSettings = baseDockerContainerSettings
}

trait DockerContainerKeys {
  lazy val createLocal = TaskKey[String]("createLocal", "Use the newly-built image and create a local container with the right parameters")
  lazy val runLocal = TaskKey[Unit]("runLocal", "Build and publish the docker container, then start it")

  lazy val dockerContainerName = settingKey[String]("Name of the container to be created. Defaults to project name")
  lazy val dockerContainerMemoryLimit = settingKey[Option[String]]("memory limit for created Docker container. e.g., Option('192M')")

  lazy val dockerContainerPortForwarding = settingKey[Map[Int, Option[Int]]]("Docker container:host port mappings")
  lazy val dockerContainerAutoForwardAllPorts = settingKey[Boolean]("Auto-map all exposed ports")
}

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
