package com.dwolla.sbt.docker.model.reified

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class DockerCreateReifiedCommandLineArgumentsSpec extends Specification {

  trait Setup extends Scope

  "DockerCreateReifiedCommandLineArguments" should {
    "convert its values to a sequence starting with create and ending with the image name" in new Setup {
      val input = DockerCreateReifiedCommandLineArguments(
        containerName = "container",
        imageName = "image",
        memoryLimit = Some("--memory 10M"),
        publishedPorts = Set("--publish 77:1983"),
        autoPublishAllPorts = Some("--publish-all"),
        linkedContainers = Set("--link a:b"),
        environment = Set("--env provided")
      )

      val output = input.toSeq

      output.head must_== "create"
      output.last must_== "image"

      output must contain("--memory 10M")
      output must contain("--publish 77:1983")
      output must contain("--publish-all")
      output must contain("--link a:b")
      output must contain("--env provided")
    }
  }
}
