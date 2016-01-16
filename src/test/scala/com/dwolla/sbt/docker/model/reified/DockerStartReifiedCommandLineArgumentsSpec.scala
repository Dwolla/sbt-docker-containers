package com.dwolla.sbt.docker.model.reified

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class DockerStartReifiedCommandLineArgumentsSpec extends Specification {

  trait Setup extends Scope

  "DockerRunReifiedCommandLineArguments" should {

    "build an ordered command line" in new Setup {
      val input = DockerStartReifiedCommandLineArguments("name")
      val expected = Seq("start", "name")
      val output = input.toSeq

      output must_== expected
    }

  }
}
