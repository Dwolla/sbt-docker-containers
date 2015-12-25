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

  lazy val defaultValues = Seq(
    dockerContainerName := name.value,
    (version in createLocal) := version.value,
    dockerContainerMemoryLimit := None
  )

  lazy val tasks = Seq(
    createLocal <<= (dockerContainerMemoryLimit, dockerTarget in Docker, dockerContainerName, publishLocal) map { (optionalMemoryLimit, imageName, containerName, _) ⇒
      val memoryLimit = optionalMemoryLimit.map(x ⇒ s"-m $x").getOrElse("")

      s"docker create --name $containerName $memoryLimit $imageName" !

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
}
