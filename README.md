# Docker Containers Plugin

SBT Plugin that adds tasks to manage Docker containers using images created by the [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager/) Docker packaging format.

Requires the [`docker`](http://docker.com) command-line tool to be on the path (although a future version may use the Docker REST API to make this unnecessary).

## Installation and Enabling

In `project/plugins.sbt`, add the following:

    addSbtPlugin("com.dwolla.sbt" % "docker-containers" % "1.0.3")

    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
    resolvers += "artifactory" at "http://artifactory.dwolla.net:8081/artifactory/repo"

Then enable the plugin in `build.sbt`. For example:

    val app = (project in file(".")).enablePlugins(DockerContainerPlugin)

This will also enable `DockerPlugin` from [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager/).

## Available Settings

### `dockerContainerMemoryLimit`

    dockerContainerMemoryLimit := Option("192M")

Sets the memory limit for the container. Maps to the `--memory` Docker command line option. This value is typically set to some value marginally higher than the JVM’s max heap size, to account for the JVM’s overhead.

### Port Publishing

#### `dockerContainerPublishAllPorts`

    dockerContainerPublishAllPorts := true

When set to true, Docker will map any exposed ports not specifically assigned to a host port to ports in the ephemeral range. This maps to the `--publish-all` Docker command line option.

#### `dockerContainerPortPublishing`

    dockerContainerPortPublishing := Map(8080 → Option(4242), 7777 → AutoAssign)

Maps exposed container ports to either specific host ports, or `AutoAssign`, which indicates the port should be published on an ephemeral port. Each mapping corresponds to a `--publish {container}:{host}` Docker command line option.

In the given example, the container port `8080` will be published on host port `4242`, and the container port `7777` will be published on a randomly assigned ephemeral port.

### Container Name

    (name in createLocalDockerContainer) := normalizedName.value

The container name will be the normalized project name by default, but this can be overridden by setting `name in createLocalDockerContainer`.

## `docker:createLocal` Task

Uses the Docker image created by `docker:publishLocal` and creates a container configured as defined.

## `docker:runLocal ` Task

Runs the container created by `docker:createLocal`.

## `docker:clean` Task

Stops and removes the container and image created by the `docker:publishLocal` and `docker:createLocal` tasks.