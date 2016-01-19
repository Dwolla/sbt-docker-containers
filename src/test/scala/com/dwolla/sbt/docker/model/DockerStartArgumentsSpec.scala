package com.dwolla.sbt.docker.model

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class DockerStartArgumentsSpec extends Specification {

  trait Setup extends Scope

  "DockerRunArguments" should {

    "build ordered command line arguments using container name" in new Setup {
      val input = DockerStartArguments("name")
      val expected = Seq("start", "name")
      val output = input.argumentSequence

      output must_== expected
    }

  }
}
