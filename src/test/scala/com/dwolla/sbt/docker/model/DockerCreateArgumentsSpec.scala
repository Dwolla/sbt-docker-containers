package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerContainerPlugin
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import reified.DockerCreateReifiedCommandLineArguments

class DockerCreateArgumentsSpec extends Specification {

  trait Setup extends Scope

  "DockerCreateArguments" should {
    "convert the raw arguments to their command-line " in new Setup {
      val input = DockerCreateArguments(
        containerName = "container",
        imageName = "image",
        memoryLimit = Some("10M"),
        publishedPorts = Map(77 → None, 1983 → Option(42)),
        autoPublishAllPorts = true,
        linkedContainers = Map("a" → "b"),
        environment = Map("env" → Option("value"), "PASS_THROUGH" → DockerContainerPlugin.autoImport.Passthru)
      )

      val expected = DockerCreateReifiedCommandLineArguments(
        containerName = "--name container",
        imageName = "image",
        memoryLimit = Some("--memory 10M"),
        publishedPorts = Set("--publish 77", "--publish 1983:42"),
        autoPublishAllPorts = Some("--publish-all"),
        linkedContainers = Set("--link a:b"),
        environment = Set("--env env=value", "--env PASS_THROUGH")
      )

      private val output = input.toDockerProcessReifiedCommandLineArguments

      output must_== expected
    }
  }

}
