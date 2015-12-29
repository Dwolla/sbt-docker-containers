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
    dockerContainerPortPublishing := Map.empty[Int, Option[Int]],
    dockerContainerLinks := Map.empty[String, String]
  )

  lazy val tasks = Seq(
    createLocalDockerContainer <<= (
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerTarget in Docker,
      name in createLocalDockerContainer,
      dockerContainerLinks,
      publishLocal in Docker
      ) map { (
                optionalMemoryLimit,
                portForwardMappings,
                autoPublishPorts,
                imageName,
                containerName, linksMap, _) ⇒

      val memoryLimit = optionalMemoryLimit.map(memoryLimitToDockerCommand).getOrElse("")
      val portForwarding = portMappings(portForwardMappings, autoPublishPorts)
      val linksCommandLine = links(linksMap)

      val commandLine = List(
        "docker create",
        setContainerName(containerName),
        memoryLimit,
        portForwarding,
        linksCommandLine,
        imageName
      ).mkString(" ")

      commandLine !

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
