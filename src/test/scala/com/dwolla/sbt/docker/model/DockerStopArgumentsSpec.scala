package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import org.specs2.mutable.Specification

class DockerStopArgumentsSpec extends Specification {

  "DockerStopArguments" should {
    "build ordered command line arguments using container name" in {
      val input = DockerStopArguments("name")
      val expected = Seq(dockerStop, "name")

      val output = input.argumentSequence

      output must_== expected
    }
  }
}


