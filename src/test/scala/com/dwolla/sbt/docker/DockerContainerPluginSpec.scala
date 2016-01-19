package com.dwolla.sbt.docker

import com.dwolla.sbt.docker.DockerContainerPlugin.{baseDockerContainerSettings, defaultValues, projectSettings, requires, tasks}
import com.typesafe.sbt.packager.docker.DockerPlugin
import model.DockerCreateArguments.ContainerName
import model.{DockerCreateArguments, DockerProcessBuilder, DockerRemoveContainerArguments, DockerRemoveImageArguments, DockerStopArguments}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import sbt._

/**
 * If any of these tests fail, make sure mockito comes AFTER specs2 on the test classpath. this seems to be an issue in IntelliJ.
 */
class DockerContainerPluginSpec extends Specification with Mockito {

  trait Setup extends Scope {
    val sbtProcessBuilder = mock[ProcessBuilder]
    val dockerProcessBuilder = mock[DockerProcessBuilder]
    val logger = mock[Logger]

    dockerProcessBuilder.toDockerProcessBuilder returns sbtProcessBuilder
  }

  "toDockerCleanProcesses" should {
    "work with unversioned image name" in {
      val expected = List(DockerStopArguments("name"), DockerRemoveContainerArguments("name", volumes = true), DockerRemoveImageArguments("image", "image:latest"))

      val output = DockerContainerPlugin.toDockerCleanProcesses("name", "image")

      output must_== expected
    }

    "work with versioned image name" in {
      val expected = List(DockerStopArguments("name"), DockerRemoveContainerArguments("name", volumes = true), DockerRemoveImageArguments("image:v1", "image:latest"))

      val output = DockerContainerPlugin.toDockerCleanProcesses("name", "image:v1")

      output must_== expected
    }
  }

  "runDockerProcess" should {
    "log the command and execute it" in new Setup {
      dockerProcessBuilder.argumentSequence returns Seq("one", "two")

      DockerContainerPlugin.runDockerProcess(dockerProcessBuilder, logger)

      there was one(logger).info("docker one two")
      there was one(sbtProcessBuilder).!!
    }
  }

  "runDockerProcessesIgnoringErrors" should {
    "run all passed processes, regardless of errors" in new Setup {
      dockerProcessBuilder.argumentSequence returns Seq("success")

      val failingSbtProcessBuilder = mock[ProcessBuilder]
      val failingDockerProcessBuilder = new DockerProcessBuilder {
        override def toDockerProcessBuilder: ProcessBuilder = failingSbtProcessBuilder
        override def argumentSequence: Seq[String] = Seq("failing", "process")
      }

      failingSbtProcessBuilder.!! throws new IntentionalTestException

      DockerContainerPlugin.runDockerProcessesIgnoringErrors(Seq(failingDockerProcessBuilder, dockerProcessBuilder), logger, Unit)

      there was one(logger).info("docker failing process")
      there was one(logger).info("docker success")
      there was one(failingSbtProcessBuilder).!!
      there was one(sbtProcessBuilder).!!
    }
  }

  "runDockerCreateAndReturnContainerName" should {
    "run docker create and return the container name" in new Setup {
      val input = mock[DockerCreateArguments]
      input.containerName returns ContainerName("name")
      input.argumentSequence returns Seq("one", "two")
      input.toDockerProcessBuilder returns sbtProcessBuilder

      val output = DockerContainerPlugin.runDockerCreateAndReturnContainerName(input, logger, Unit)

      output must_== "name"

      there was one(logger).info("docker one two")
      there was one(sbtProcessBuilder).!!
    }
  }

  "projectSettings" should {
    "be exposed as a project-specific key" in {
      baseDockerContainerSettings must_== defaultValues ++ tasks
    }

    "project-specific key must be assigned to sbt projectSettings" in {
      projectSettings must_== baseDockerContainerSettings
    }
  }

  "plugin dependencies" should {
    "include DockerPlugin" in {
      requires must_== DockerPlugin
    }
  }
}

class IntentionalTestException extends RuntimeException("this exception was thrown intentionally in a test", null, true, false)
