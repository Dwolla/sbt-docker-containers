package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions._
import org.specs2.mutable.Specification
import sbt.Process

class DockerProcessBuilderSpec extends Specification {

  "DockerProcessBuilder.toDockerProcessBuilder" should {
    "build an SBT Process with the Docker command and the given arguments" in {
      val test = new DockerProcessBuilder {
        override def argumentSequence: Seq[String] = Seq("one", "two")
      }

      test.toDockerProcessBuilder.toString must_== Process(dockerCommand, Seq("one", "two")).toString
    }
  }
}
