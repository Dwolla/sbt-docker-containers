package com.dwolla.sbt.docker.model

import org.specs2.mutable.Specification
import reified.DockerRemoveContainerReifiedCommandLineArguments

class DockerRemoveContainerArgumentsSpec extends Specification {

  "DockerRemoveContainerArguments" should {
    "reify arguments when volumes should be removed" in {
      val input = DockerRemoveContainerArguments("name", volumes = true)
      val expected = DockerRemoveContainerReifiedCommandLineArguments("name", Some("--volumes=true"))

      val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }

    "reify arguments when volumes should not be removed" in {
      val input = DockerRemoveContainerArguments("name", volumes = false)
      val expected = DockerRemoveContainerReifiedCommandLineArguments("name", Some("--volumes=false"))

      val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }
  }

}
