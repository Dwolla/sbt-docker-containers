package com.dwolla.sbt.docker.model.reified

import com.dwolla.sbt.docker.DockerCommandLineOptions.dockerStop
import org.specs2.mutable.Specification

class DockerStopReifiedCommandLineArgumentsSpec extends Specification {

  "DockerStopReifiedCommandLineArguments" should {
    "convert reified arguments to ordered command line arguments" in {
      val input = DockerStopReifiedCommandLineArguments("name")
      val expected = Seq(dockerStop, "name")

      val output = input.toSeq

      output must_== expected
    }
  }
}
