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
    dockerContainerName := normalizedName.value,
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
