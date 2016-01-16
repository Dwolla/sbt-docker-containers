package com.dwolla.sbt.docker.model

import org.specs2.mutable.Specification
import reified.DockerRemoveImageReifiedCommandLineArguments

class DockerRemoveImageArgumentsSpec extends Specification {
  "DockerRemoveImageArguments" should {
    "reify arguments" in {
      val input = DockerRemoveImageArguments("image1", "image2", "image1")
      val expected = DockerRemoveImageReifiedCommandLineArguments(Set("image1", "image2"))

      val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }
  }
}
