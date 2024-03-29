# Docker Containers Plugin

![Dwolla/sbt-docker-containers CI](https://github.com/Dwolla/sbt-docker-containers/actions/workflows/ci.yml/badge.svg)
[![license](https://img.shields.io/github/license/Dwolla/sbt-docker-containers.svg?maxAge=2592000&style=flat-square)]()

SBT Plugin that adds tasks to manage Docker containers using images created by the [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager/) Docker packaging format.

Requires the [`docker`](http://docker.com) command-line tool to be on the path.

## Installation and Enabling

In `project/plugins.sbt`, add the following:

```scala
addSbtPlugin("com.dwolla.sbt" % "docker-containers" % "***VERSION***")
```

Then enable the plugin in `build.sbt`. For example:

```scala
val app = (project in file(".")).enablePlugins(DockerContainerPlugin)
```

This will also enable `DockerPlugin` from [sbt-native-packager](http://www.scala-sbt.org/sbt-native-packager/).

## Available Settings

### Container Memory Limitation

```scala
dockerContainerMemoryLimit := Option("192M")
```

Sets the memory limit for the container. Maps to the `--memory` Docker command line option.

### Port Publishing

#### `dockerContainerPublishAllPorts`

```scala
dockerContainerPublishAllPorts := true
```

When set to true, Docker will map any exposed ports not specifically assigned to a host port to ports in the ephemeral range. This maps to the `--publish-all` Docker command line option.

#### `dockerContainerPortPublishing`

```scala
dockerContainerPortPublishing := Map(8080 → Option(4242), 7777 → AutoAssign)
```

Maps exposed container ports to either specific host ports, or `AutoAssign`, which indicates the port should be published on an ephemeral port. Each mapping corresponds to a `--publish {container}:{host}` Docker command line option.

In the given example, the container port `8080` will be published on host port `4242`, and the container port `7777` will be published on a randomly assigned ephemeral port.

### Container Name

```scala
(name in createLocalDockerContainer) := normalizedName.value
```

The container name will be the normalized project name by default, but this can be overridden by setting `name in createLocalDockerContainer`.

### Linked Containers

```scala
dockerContainerLinks := Map("container-name" → "container-name")
```

Creates links from the new container to the specified container. The key side of the mapping is the actual container name, while the value side is the alias inside the new container. Typically, the alias matches the container name, but it can be different if needed.

Links can also be added using the syntax below:

```scala
dockerContainerLinks += "container-name" → "container-name"
dockerContainerLinks ++= Map("container-name" → "container-name", "container-two" → "container-two")
```

Both of these examples will append new linked containers to any previously established in the build definition.

### Additional Environment Variables

```scala
dockerContainerAdditionalEnvironmentVariables := Map("NAME" → Option("value"), "FROM_HOST" → Passthru)
```

Operators can augment or override the environment specified by the container’s image by giving mappings from the name of the environment variable to either the value or `Passthru`.

If `Passthru` is specified and the variable name is present in the host’s environment, the value in the container will be set to match that of the host. If the variable is not set in the host’s environment, the variable will be unset in the container (even if it is set by the image). Docker’s behavior is [documented here](https://docs.docker.com/engine/reference/commandline/run/#set-environment-variables-e-env-env-file), but supplying an environment file is not supported by this plugin at this time.

## `docker:createLocal` Task

Uses the Docker image created by `docker:publishLocal` and creates a container configured as defined.

## `docker:startLocal ` Task

Runs the container created by `docker:createLocal`. This action is also available using `docker:runLocal`, but this is deprecated and will be removed in a future version of the plugin.

## `docker:clean` Task

Stops and removes the container and image created by the `docker:publishLocal` and `docker:createLocal` tasks.
