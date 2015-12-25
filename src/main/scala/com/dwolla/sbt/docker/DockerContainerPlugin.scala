package com.dwolla.sbt.docker

import com.typesafe.sbt.packager.docker.DockerPlugin
import sbt.Keys._
import sbt._

import scala.language.postfixOps

object DockerContainerPlugin extends AutoPlugin {

  object autoImport extends DockerContainerKeys

  import DockerPlugin.autoImport._
  import autoImport._

  override def requires = DockerPlugin

  lazy val baseDockerContainerSettings = Seq(
    dockerContainerName := name.value,
    (version in createLocal) := version.value,
    dockerContainerMemoryLimit := None,

    createLocal := {
      val imageName = (dockerTarget in Docker).value
      (publishLocal in Docker).value

      val memoryLimit = dockerContainerMemoryLimit.value.map(x ⇒ s"-m $x").getOrElse("")

      s"docker create --name ${dockerContainerName.value} $memoryLimit $imageName" !

      dockerContainerName.value
    },
    runLocal := {
      s"docker start ${createLocal.value}" !
    },
    clean := {
      clean.value
      val containerName = dockerContainerName.value
      val imageName = (dockerTarget in Docker).value
      s"docker stop $containerName" !;
      s"docker rm $containerName" !;
      s"docker rmi $imageName" !
    })

  override lazy val projectSettings = baseDockerContainerSettings
}

trait DockerContainerKeys {
  lazy val createLocal = TaskKey[String]("createLocal", "Use the newly-built image and create a local container with the right parameters")
  lazy val runLocal = TaskKey[Unit]("runLocal", "build and publish the docker container, then start it")
  lazy val cleanDocker = TaskKey[Unit]("clean", "stop the service, remove its container and image")

  lazy val dockerContainerName = settingKey[String]("Name of the container to be created. Defaults to project name")
  lazy val dockerContainerMemoryLimit = settingKey[Option[String]]("memory limit for created Docker container. e.g., Option('192M')")
}
