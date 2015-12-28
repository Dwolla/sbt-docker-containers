package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import sbt.Keys._
import sbt._

import scala.language.postfixOps

object DockerContainerPlugin extends AutoPlugin {

  object autoImport extends DockerContainerKeys

  import DockerCommandLineOptions._
  import DockerPlugin.autoImport._
  import autoImport._

  override def requires = DockerPlugin

  lazy val defaultValues = Seq(
    (name in createLocalDockerContainer) := normalizedName.value,
    dockerContainerMemoryLimit := None,
    dockerContainerPublishAllPorts := false,
    dockerContainerPortPublishing := Map.empty[Int, Option[Int]]
  )

  lazy val tasks = Seq(
    createLocalDockerContainer <<= (
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerTarget in Docker,
      name in createLocalDockerContainer,
      publishLocal in Docker
      ) map { (
                optionalMemoryLimit,
                portForwardMappings,
                autoForwardPorts,
                imageName,
                containerName, _) ⇒

      val memoryLimit = optionalMemoryLimit.map(memoryLimitToDockerCommand).getOrElse("")
      val portForwarding = portMappings(portForwardMappings, autoForwardPorts)

      s"docker create ${setContainerName(containerName)} $portForwarding $memoryLimit $imageName" !

      containerName
    },
    runLocalDockerContainer <<= createLocalDockerContainer map { containerName ⇒
      s"docker start $containerName" !
    },
    clean in Docker <<= (name in createLocalDockerContainer, dockerTarget in Docker, clean) map { (containerName, imageName, _) ⇒
      s"docker stop $containerName" !;
      s"docker rm $containerName" !;
      s"docker rmi $imageName" !
    })

  lazy val baseDockerContainerSettings = defaultValues ++ tasks

  override lazy val projectSettings = baseDockerContainerSettings
}
