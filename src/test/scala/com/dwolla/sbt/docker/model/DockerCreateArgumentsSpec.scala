package com.dwolla.sbt.docker.model

import DockerCreateArguments._
import com.dwolla.sbt.docker.DockerContainerPlugin
import com.dwolla.testutils.matchers.AdditionalSeqMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class DockerCreateArgumentsSpec extends Specification with AdditionalSeqMatchers {

  trait Setup extends Scope

  "DockerCreateArguments" should {
    "convert the raw arguments to their sequenced command-line form" in new Setup {
      val input = DockerCreateArguments(
        containerName = ContainerName("container"),
        imageName = "image",
        memoryLimit = Some(MemoryLimit("10M")),
        publishedPorts = PublishedPorts(77 → DockerContainerPlugin.autoImport.AutoAssign, 1983 → Option(42)),
        autoPublishAllPorts = true,
        linkedContainers = LinkedContainers("a" → "b"),
        environment = Environment("env" → Option("value"), "PASS_THROUGH" → DockerContainerPlugin.autoImport.Passthru),
        network = Some(NetworkName("testnetwork"))
      )

      val output = input.argumentSequence

      output must startWith("create")
      output must endWith("image")

      output must containSlice("--name", "container")
      output must containSlice("--memory", "10M")
      output must containSlice("--publish", "77")
      output must containSlice("--publish", "1983:42")
      output must contain("--publish-all")
      output must containSlice("--link", "a:b")
      output must containSlice("--env", "env=value")
      output must containSlice("--env", "PASS_THROUGH")
      output must containSlice("--network", "testnetwork")
    }

    "convert SBT types to case class" in new Setup {
      val expected = DockerCreateArguments(
        containerName = ContainerName("container"),
        imageName = "image",
        memoryLimit = Some(MemoryLimit("10M")),
        publishedPorts = PublishedPorts(77 → DockerContainerPlugin.autoImport.AutoAssign, 1983 → Option(42)),
        autoPublishAllPorts = true,
        linkedContainers = LinkedContainers("a" → "b"),
        environment = Environment("env" → Option("value"), "PASS_THROUGH" → DockerContainerPlugin.autoImport.Passthru),
        network = Some(NetworkName("testnetwork"))
      )

      val output = DockerCreateArguments.fromBasicSbtTypes(
        containerName = "container",
        imageName = "image",
        memoryLimit = Some("10M"),
        portPublishing = Map(77 → DockerContainerPlugin.autoImport.AutoAssign, 1983 → Option(42)),
        publishAllPorts = true,
        links = Map("a" → "b"),
        env = Map("env" → Option("value"), "PASS_THROUGH" → DockerContainerPlugin.autoImport.Passthru),
        network = Some("testnetwork")
      )

      output must_== expected
    }
  }

}
