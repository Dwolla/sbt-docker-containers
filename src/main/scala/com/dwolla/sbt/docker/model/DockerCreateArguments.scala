package com.dwolla.sbt.docker.model

import com.dwolla.sbt.docker.DockerCommandLineOptions
import com.dwolla.sbt.docker.DockerCommandLineOptions._
import com.dwolla.sbt.docker.model.DockerCreateArguments._

case class DockerCreateArguments(containerName: ContainerName,
                                 imageName: String,
                                 memoryLimit: Option[MemoryLimit],
                                 publishedPorts: PublishedPorts,
                                 autoPublishAllPorts: Boolean,
                                 linkedContainers: LinkedContainers,
                                 environment: Environment)
  extends DockerProcessBuilder {

  override def argumentSequence: Seq[String] = (Some(DockerCommandLineOptions.dockerCreate) ++
    Seq(containerName,
      publishedPorts,
      linkedContainers,
      environment
    ).flatMap(_.reified) ++
    publishAllExposedPorts ++
    memoryLimitToDockerCommand ++
    Some(imageName)).toSeq

  private def publishAllExposedPorts: Option[String] = if (autoPublishAllPorts) Some(publishAllPorts) else None

  private def memoryLimitToDockerCommand = memoryLimit.map(_.reified).getOrElse(missing)
}

object DockerCreateArguments {

  def fromBasicSbtTypes(containerName: String,
                        imageName: String,
                        memoryLimit: Option[String],
                        portPublishing: Map[Int, Option[Int]],
                        publishAllPorts: Boolean,
                        links: Map[String, String],
                        env: Map[String, Option[String]]): DockerCreateArguments =
    DockerCreateArguments(
      ContainerName(containerName),
      imageName,
      memoryLimit.map(MemoryLimit),
      PublishedPorts(portPublishing.toSeq: _*),
      publishAllPorts,
      LinkedContainers(links.toSeq: _*),
      Environment(env.toSeq: _*)
    )

  private val missing = Seq.empty[String]

  trait Argument {
    def reified: Seq[String]
  }

  trait MappingArgument extends Argument {

    val definedMapping: Seq[String]
    val argumentLabel: String

    override def reified = definedMapping.distinct.flatMap(Seq(argumentLabel, _))
  }

  case class ContainerName(name: String) extends Argument {
    override def reified: Seq[String] = Seq(containerName, name)
  }

  case class MemoryLimit(limit: String) extends Argument {
    override def reified: Seq[String] = Seq(memory, limit)
  }

  case class PublishedPorts(publishedPorts: (Int, Option[Int])*) extends MappingArgument {
    val argumentLabel = publishPort

    override lazy val definedMapping: Seq[String] = publishedPorts.map {
      case (container, maybeHost) ⇒
        val host = maybeHost.map(host ⇒ s":$host").getOrElse("")
        s"$container$host"
    }
  }

  case class LinkedContainers(links: (String, String)*) extends MappingArgument {
    val argumentLabel = link

    override lazy val definedMapping: Seq[String] = links.map {
      case (name, alias) ⇒ s"$name:$alias"
    }
  }

  case class Environment(env: (String, Option[String])*) extends MappingArgument {
    override lazy val definedMapping: Seq[String] = env.map {
      case (key, maybeValue) ⇒
        val value = maybeValue.map(host ⇒ s":$host").getOrElse("")
        s"$key$value"
    }
    override val argumentLabel: String = environment
  }

}
