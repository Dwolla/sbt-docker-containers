package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import model.{DockerStartArguments, DockerCreateArguments}
import sbt.Keys._
import sbt._

import scala.language.postfixOps
import util.Try

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

  lazy val dockerCreateArguments = TaskKey[DockerCreateArguments]("dockerCreateArguments", "task key used internally for testing the createLocal task") in Docker
  lazy val dockerStartArguments = TaskKey[DockerStartArguments]("dockerStartArguments", "task key used internally for testing the createLocal task") in Docker
  lazy val dockerCleanArguments = TaskKey[List[ProcessBuilder]]("dockerCleanArguments", "task key used internally for testing the createLocal task") in Docker

  lazy val tasks = Seq(
    dockerCreateArguments <<= (
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerTarget in Docker,
      name in createLocalDockerContainer,
      dockerContainerLinks
      ) map { (
                optionalMemoryLimit,
                portPublishingMappings,
                autoPublishPorts,
                imageName,
                containerName, linksMap) ⇒
      DockerCreateArguments(containerName, imageName, optionalMemoryLimit, portPublishingMappings, autoPublishPorts, linksMap)
    },
    createLocalDockerContainer <<= (dockerCreateArguments, publishLocal in Docker) map { (dockerCreateArguments, _) ⇒
      dockerCreateArguments.toDockerProcessReifiedCommandLineArguments.toDockerProcessBuilder !!

      dockerCreateArguments.containerName
    },

    dockerStartArguments <<= createLocalDockerContainer map { containerName ⇒
      DockerStartArguments(containerName)
    },
    startLocalDockerContainer <<= dockerStartArguments map { arguments ⇒
      arguments.toDockerProcessReifiedCommandLineArguments.toSeq !!
    },
    runLocalDockerContainer <<= startLocalDockerContainer,

    dockerCleanArguments <<= (name in createLocalDockerContainer, dockerTarget in Docker) map { (containerName, fullImageName) ⇒
      val allImageNames: List[String] = fullImageName +: Try {
        fullImageName.split(":")(0) + ":latest"
      }.toOption.toList

      List(
        dockerProcess("stop", containerName),
        dockerProcess("rm", "-v", containerName)
      ) ++ allImageNames.map(dockerProcess("rmi", _))
    },
    clean in Docker <<= (dockerCleanArguments, clean) map { (processBuilders, _) ⇒
      processBuilders.foreach { _ ! }
    })

  lazy val baseDockerContainerSettings = defaultValues ++ tasks

  override lazy val projectSettings = baseDockerContainerSettings

  def dockerProcess(args: String*): ProcessBuilder = Process(dockerCommand, args)
}
