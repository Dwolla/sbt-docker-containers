package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import model.{DockerStartArguments, DockerCreateArguments, DockerProcessBuilder, DockerProcessReifiedCommandLineArgumentBuilder, DockerRemoveContainerArguments, DockerRemoveImageArguments, DockerStopArguments}
import sbt.Keys._
import sbt._

import scala.language.postfixOps
import util.Try

object DockerContainerPlugin extends AutoPlugin {

  object autoImport extends DockerContainerKeys

  import DockerPlugin.autoImport._
  import autoImport._

  override def requires = DockerPlugin

  lazy val defaultValues = Seq(
    (name in createLocalDockerContainer) := normalizedName.value,
    dockerContainerMemoryLimit := None,
    dockerContainerPublishAllPorts := false,
    dockerContainerPortPublishing := Map.empty[Int, Option[Int]],
    dockerContainerLinks := Map.empty[String, String],
    dockerContainerAdditionalEnvironmentVariables := Map.empty[String, Option[String]]
  )

  lazy val dockerCreateArguments = TaskKey[DockerCreateArguments]("dockerCreateArguments", "task key used internally for testing the createLocal task") in Docker
  lazy val dockerStartArguments = TaskKey[DockerStartArguments]("dockerStartArguments", "task key used internally for testing the startLocal task") in Docker
  lazy val dockerCleanArguments = TaskKey[Seq[DockerProcessReifiedCommandLineArgumentBuilder[_ <: DockerProcessBuilder]]]("dockerCleanArguments", "task key used internally for testing the docker:clean task") in Docker

  lazy val tasks = Seq(
    dockerCreateArguments <<= (
      name in createLocalDockerContainer,
      dockerTarget in Docker,
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerContainerLinks,
      dockerContainerAdditionalEnvironmentVariables
      ) map DockerCreateArguments.apply,
    createLocalDockerContainer <<= (dockerCreateArguments, publishLocal in Docker) map runDockerCreateAndReturnContainerName,

    dockerStartArguments <<= createLocalDockerContainer map DockerStartArguments.apply,
    startLocalDockerContainer <<= dockerStartArguments map runDockerProcess,
    runLocalDockerContainer <<= startLocalDockerContainer,

    dockerCleanArguments <<= (name in createLocalDockerContainer, dockerTarget in Docker) map toDockerCleanProcesses,
    clean in Docker <<= (dockerCleanArguments, clean) map runDockerProcesses
  )

  def runDockerProcess(processBuilder: DockerProcessReifiedCommandLineArgumentBuilder[_ <: DockerProcessBuilder]): Unit =
    processBuilder.toDockerProcessReifiedCommandLineArguments.toDockerProcessBuilder !!

  def runDockerProcesses(processBuilders: Seq[DockerProcessReifiedCommandLineArgumentBuilder[_ <: DockerProcessBuilder]], unit: Unit): Unit =
    processBuilders.foreach(runDockerProcess)

  def runDockerCreateAndReturnContainerName(dockerCreateArguments: DockerCreateArguments, unit: Unit): String = {
    dockerCreateArguments.toDockerProcessReifiedCommandLineArguments.toDockerProcessBuilder !!

    dockerCreateArguments.containerName
  }

  def toDockerCleanProcesses(containerName: String, fullImageName: String) = {
    val allImageNames: List[String] = fullImageName +: Try {
      fullImageName.split(":")(0) + ":latest"
    }.toOption.toList

    Seq(
      DockerStopArguments(containerName),
      DockerRemoveContainerArguments(containerName, volumes = true),
      DockerRemoveImageArguments(allImageNames: _*)
    )
  }

  lazy val baseDockerContainerSettings = defaultValues ++ tasks

  override lazy val projectSettings = baseDockerContainerSettings
}
