package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions.removeContainer
import org.specs2.mutable.Specification

class DockerRemoveContainerReifiedCommandLineArgumentsSpec extends Specification {

  "DockerRemoveContainerReifiedCommandLineArguments" should {
    "convert reified arguments to ordered command line when volumes is provided" in {
      val input = DockerRemoveContainerReifiedCommandLineArguments("name", Some("--volumes=true"))
      val expected = Seq(removeContainer, "--volumes=true", "name")

      val output = input.toSeq

      output must_== expected
    }

    "convert reified arguments to ordered command line when volumes is not provided" in {
      val input = DockerRemoveContainerReifiedCommandLineArguments("name", None)
      val expected = Seq(removeContainer, "name")

      val output = input.toSeq

      output must_== expected
    }
  }
}
