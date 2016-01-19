package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import org.specs2.mutable.Specification

class DockerRemoveContainerArgumentsSpec extends Specification {

  "DockerRemoveContainerArguments" should {
    "build ordered command line arguments when volumes should be removed" in {
      val input = DockerRemoveContainerArguments("name", volumes = true)
      val expected = Seq(removeContainer, "--volumes=true", "name")

      val output = input.argumentSequence

      output must_== expected
    }

    "build ordered command line arguments when volumes should not be removed" in {
      val input = DockerRemoveContainerArguments("name", volumes = false)
      val expected = Seq(removeContainer, "--volumes=false",  "name")

      val output = input.argumentSequence

      output must_== expected
    }
  }

}
