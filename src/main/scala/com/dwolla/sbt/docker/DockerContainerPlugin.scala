package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import model.{DockerCreateArguments, DockerProcessBuilder, DockerRemoveContainerArguments, DockerRemoveImageArguments, DockerStartArguments, DockerStopArguments}
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
  lazy val dockerCleanArguments = TaskKey[Seq[DockerProcessBuilder]]("dockerCleanArguments", "task key used internally for testing the docker:clean task") in Docker
  lazy val logger = TaskKey[Logger]("logger", "logger from streams") in Docker

  lazy val tasks = Seq(
    logger <<= streams map { (streams) ⇒ streams.log },
    dockerCreateArguments <<= (
      name in createLocalDockerContainer,
      dockerTarget in Docker,
      dockerContainerMemoryLimit,
      dockerContainerPortPublishing,
      dockerContainerPublishAllPorts,
      dockerContainerLinks,
      dockerContainerAdditionalEnvironmentVariables
      ) map DockerCreateArguments.fromBasicSbtTypes,
    createLocalDockerContainer <<= (dockerCreateArguments, logger, publishLocal in Docker) map runDockerCreateAndReturnContainerName,

    dockerStartArguments <<= createLocalDockerContainer map DockerStartArguments.apply,
    startLocalDockerContainer <<= (dockerStartArguments, logger) map runDockerProcess,
    runLocalDockerContainer <<= startLocalDockerContainer,

    dockerCleanArguments <<= (name in createLocalDockerContainer, dockerTarget in Docker) map toDockerCleanProcesses,
    clean in Docker <<= (dockerCleanArguments, logger, clean) map runDockerProcessesIgnoringErrors
  )

  def runDockerProcess(processBuilder: DockerProcessBuilder, logger: Logger): Unit = {
    logger.info((DockerCommandLineOptions.dockerCommand +: processBuilder.argumentSequence).mkString(" "))
    processBuilder.toDockerProcessBuilder !!
  }

  def runDockerProcessesIgnoringErrors(processBuilders: Seq[DockerProcessBuilder], logger: Logger, unit: Unit): Unit =
    processBuilders.foreach(proc ⇒ Try {
      runDockerProcess(proc, logger)
    })

  def runDockerCreateAndReturnContainerName(dockerCreateArguments: DockerCreateArguments, logger: Logger, unit: Unit): String = {
    runDockerProcess(dockerCreateArguments, logger)

    dockerCreateArguments.containerName.name
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
