package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions
import com.dwolla.sbt.docker.DockerCommandLineOptions.removeImage
import org.specs2.mutable.Specification

class DockerRemoveImageReifiedCommandLineArgumentsSpec extends Specification {
  "DockerRemoveImageReifiedCommandLineArguments" should {
    "convert reified arguments into ordered command line" in {
      val input = DockerRemoveImageReifiedCommandLineArguments(Set("image1", "image2"))
      val expected = removeImage +: Set("image1", "image2").toSeq

      val output = input.toSeq

      output must_== expected
    }
  }
}
