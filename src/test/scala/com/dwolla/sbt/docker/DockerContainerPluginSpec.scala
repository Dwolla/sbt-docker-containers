package com.dwolla.sbt.docker

import model.{DockerCreateArguments, DockerStopArguments, DockerRemoveContainerArguments, DockerRemoveImageArguments}
import org.specs2.mutable.Specification

class DockerContainerPluginSpec extends Specification {

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
}
