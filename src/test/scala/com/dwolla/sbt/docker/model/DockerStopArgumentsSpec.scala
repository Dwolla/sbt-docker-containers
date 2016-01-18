package com.dwolla.sbt.docker.model

import org.specs2.mutable.Specification
import reified.DockerStopReifiedCommandLineArguments

class DockerStopArgumentsSpec extends Specification {

  "DockerStopArguments" should {
    "reify container name" in {
      val input = DockerStopArguments("name")
      val expected = DockerStopReifiedCommandLineArguments("name")

      expected must beAnInstanceOf[DockerProcessBuilder]

      val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }
  }
}


