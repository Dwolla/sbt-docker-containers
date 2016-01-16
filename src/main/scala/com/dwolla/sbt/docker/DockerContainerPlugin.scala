package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import model.DockerCreateArguments
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

  lazy val createLocalProcessBuilder = TaskKey[DockerCreateArguments]("createLocalProcessBuilder", "task key used internally for testing the createLocal task") in Docker
  lazy val runLocalProcessBuilder = TaskKey[ProcessBuilder]("runLocalProcessBuilder", "task key used internally for testing the createLocal task") in Docker
  lazy val cleanProcessBuilders = TaskKey[List[ProcessBuilder]]("cleanProcessBuilder", "task key used internally for testing the createLocal task") in Docker
  
  lazy val tasks = Seq(
    createLocalProcessBuilder <<= (
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerTarget in Docker,
      name in createLocalDockerContainer,
      dockerContainerLinks,
      publishLocal in Docker
      ) map { (
                optionalMemoryLimit,
                portPublishingMappings,
                autoPublishPorts,
                imageName,
                containerName, linksMap, _) ⇒
      DockerCreateArguments(containerName, imageName, optionalMemoryLimit, portPublishingMappings, autoPublishPorts, linksMap)
    },
    createLocalDockerContainer <<= createLocalProcessBuilder map { (dockerCreateArguments) ⇒
      dockerCreateArguments.toDockerProcessReifiedCommandLineArguments.toDockerProcessBuilder !!

      dockerCreateArguments.containerName
    },

    runLocalProcessBuilder <<= createLocalDockerContainer map { containerName ⇒
      dockerProcess("start", containerName)
    },
    runLocalDockerContainer <<= runLocalProcessBuilder map { processBuilder ⇒
      processBuilder !!
    },

    cleanProcessBuilders <<= (name in createLocalDockerContainer, dockerTarget in Docker) map { (containerName, fullImageName) ⇒
      val allImageNames: List[String] = fullImageName +: Try {
        fullImageName.split(":")(0) + ":latest"
      }.toOption.toList

      List(
        dockerProcess("stop", containerName),
        dockerProcess("rm", "-v", containerName)
      ) ++ allImageNames.map(dockerProcess("rmi", _))
    },
    clean in Docker <<= (cleanProcessBuilders, clean) map { (processBuilders, _) ⇒
      processBuilders.foreach { _ ! }
    })

  lazy val baseDockerContainerSettings = defaultValues ++ tasks

  override lazy val projectSettings = baseDockerContainerSettings

  def dockerProcess(args: String*): ProcessBuilder = Process(dockerCommand, args)
}
