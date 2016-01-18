package com.dwolla.sbt.docker.model

import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import reified.DockerStartReifiedCommandLineArguments

class DockerStartArgumentsSpec extends Specification {

  trait Setup extends Scope

  "DockerRunArguments" should {

    "reify its arguments" in new Setup {
      val input = DockerStartArguments("name")
      val expected = DockerStartReifiedCommandLineArguments("name")

      val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }

  }
}
