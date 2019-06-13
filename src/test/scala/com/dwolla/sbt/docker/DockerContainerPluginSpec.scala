package com.dwolla.sbt.docker

import com.dwolla.sbt.docker.DockerContainerPlugin._
import com.dwolla.sbt.docker.model.DockerCreateArguments.{ContainerName, LinkedContainers, PublishedPorts}
import com.dwolla.sbt.docker.model._
import com.typesafe.sbt.packager.docker.DockerPlugin
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import sbt._
import sbt.util.{Level, _}

import scala.concurrent.Promise
import scala.sys.process._

class DockerContainerPluginSpec(implicit ee: ExecutionEnv) extends Specification  {

  trait Setup extends Scope {
    val sbtProcessBuilder: FakeProcessBuilder = new FakeProcessBuilder
    def dockerProcessBuilder(arguments: Seq[String]): DockerProcessBuilder = new FakeDockerProcessBuilder(sbtProcessBuilder, arguments)
    val logger: FakeLogger = new FakeLogger
  }

  "toDockerCleanProcesses" should {
    "work with unversioned image name" in {
      val expected = List(DockerStopArguments("name"), DockerRemoveContainerArguments("name", volumes = true), DockerRemoveImageArguments("image", "image:latest"))

      val output = DockerContainerPlugin.toDockerCleanProcesses("name", "image")

      output must_== expected
    }

    "work with versioned image name" in {
      val expected = List(DockerStopArguments("name"), DockerRemoveContainerArguments("name", volumes = true), DockerRemoveImageArguments("image:v1", "image:latest"))

      val output = DockerContainerPlugin.toDockerCleanProcesses("name", "image:v1")

      output must_== expected
    }
  }

  "runDockerProcess" should {
    "log the command and execute it" in new Setup {
      DockerContainerPlugin.runDockerProcess(dockerProcessBuilder(Seq("one", "two")), logger)

      logger.loggedMessages must contain((Level.Info, "docker one two"))
      sbtProcessBuilder.wasExecuted.future must be_==(()).await
    }
  }

  "runDockerProcessesIgnoringErrors" should {
    "run all passed processes, regardless of errors" in new Setup {
      val failingSbtProcessBuilder = new FakeProcessBuilder {
        override def !! : String = {
          super.!!
          throw new IntentionalTestException
        }
      }
      val failingDockerProcessBuilder = new DockerProcessBuilder {
        override def toDockerProcessBuilder: ProcessBuilder = failingSbtProcessBuilder
        override def argumentSequence: Seq[String] = Seq("failing", "process")
      }

      DockerContainerPlugin.runDockerProcessesIgnoringErrors(Seq(failingDockerProcessBuilder, dockerProcessBuilder(Seq("success"))), logger, Unit)

      logger.loggedMessages must contain((Level.Info, "docker failing process"))
      logger.loggedMessages must contain((Level.Info, "docker success"))
      sbtProcessBuilder.wasExecuted.future must be_==(()).await
      failingSbtProcessBuilder.wasExecuted.future must be_==(()).await
    }
  }

  "runDockerCreateAndReturnContainerName" should {
    "run docker create and return the container name" in new Setup {

      val input = new DockerCreateArguments(ContainerName("name"), "any", None, PublishedPorts(), true, LinkedContainers(), DockerCreateArguments.Environment()) {
        override val argumentSequence = Seq("one", "two")

        override def toDockerProcessBuilder: ProcessBuilder = sbtProcessBuilder
      }

      val output = DockerContainerPlugin.runDockerCreateAndReturnContainerName(input, logger, Unit)

      output must_== "name"

      logger.loggedMessages must contain((Level.Info, "docker one two"))
      sbtProcessBuilder.wasExecuted.future must be_==(()).await
    }
  }

  "projectSettings" should {
    "be exposed as a project-specific key" in {
      baseDockerContainerSettings must_== defaultValues ++ tasks
    }

    "project-specific key must be assigned to sbt projectSettings" in {
      projectSettings must_== baseDockerContainerSettings
    }
  }

  "plugin dependencies" should {
    "include DockerPlugin" in {
      requires must_== DockerPlugin
    }
  }
}

class IntentionalTestException extends RuntimeException("this exception was thrown intentionally in a test", null, true, false)

class FakeProcessBuilder extends ProcessBuilder {
  val wasExecuted: scala.concurrent.Promise[Unit] = Promise[Unit]()

  override def !! : String = {
    wasExecuted.success(())

    "hurray"
  }

  override def !!(log: ProcessLogger): String = ???

  override def !!< : String = ???

  override def !!<(log: ProcessLogger): String = ???

  override def lineStream: Stream[String] = ???

  override def lineStream(log: ProcessLogger): Stream[String] = ???

  override def lineStream_! : Stream[String] = ???

  override def lineStream_!(log: ProcessLogger): Stream[String] = ???

  override def ! : Int = ???

  override def !(log: ProcessLogger): Int = ???

  override def !< : Int = ???

  override def !<(log: ProcessLogger): Int = ???

  override def run(): Process = ???

  override def run(log: ProcessLogger): Process = ???

  override def run(io: ProcessIO): Process = ???

  override def run(connectInput: Boolean): Process = ???

  override def run(log: ProcessLogger, connectInput: Boolean): Process = ???

  override def #&&(other: ProcessBuilder): ProcessBuilder = ???

  override def #||(other: ProcessBuilder): ProcessBuilder = ???

  override def #|(other: ProcessBuilder): ProcessBuilder = ???

  override def ###(other: ProcessBuilder): ProcessBuilder = ???

  override def canPipeTo: Boolean = ???

  override def hasExitValue: Boolean = ???

  override protected def toSource: ProcessBuilder = ???

  override protected def toSink: ProcessBuilder = ???
}

class FakeLogger extends AbstractLogger {
  val loggedMessages: collection.mutable.ListBuffer[(Level.Value, String)] = collection.mutable.ListBuffer.empty

  override def getLevel = ???

  override def setLevel(newLevel: Level.Value): Unit = ???

  override def setTrace(flag: Int): Unit = ???

  override def getTrace: Int = ???

  override def successEnabled: Boolean = ???

  override def setSuccessEnabled(flag: Boolean): Unit = ???

  override def control(event: util.ControlEvent.Value, message: => String): Unit = ???

  override def logAll(events: Seq[LogEvent]): Unit = ???

  override def trace(t: => Throwable): Unit = ???

  override def success(message: => String): Unit = ???

  override def log(level: Level.Value, message: => String): Unit = loggedMessages.+=((level, message))
}

class FakeDockerProcessBuilder(processBuilder: ProcessBuilder, override val argumentSequence: Seq[String]) extends DockerProcessBuilder {
  var providedArgumentSequence = Seq.empty[String]

  override def toDockerProcessBuilder: ProcessBuilder = processBuilder
}