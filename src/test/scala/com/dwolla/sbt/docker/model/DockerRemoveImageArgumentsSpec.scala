package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import org.specs2.mutable.Specification

class DockerRemoveImageArgumentsSpec extends Specification {
  "DockerRemoveImageArguments" should {
    "build ordered command line arguments using image names" in {
      val input = DockerRemoveImageArguments("image1", "image2", "image1")
      val expected = removeImage +: Set("image1", "image2").toSeq

      val output = input.argumentSequence

      output must_== expected
    }
  }
}
