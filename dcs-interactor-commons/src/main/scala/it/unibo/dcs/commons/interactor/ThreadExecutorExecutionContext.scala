package it.unibo.dcs.commons.interactor

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.core.Vertx
import it.unibo.dcs.commons.interactor.executor.ThreadExecutor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

final class ThreadExecutorExecutionContext(vertx: Vertx) extends ThreadExecutor {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  override def execute(runnable: Runnable): Unit = vertx.executeBlocking[Unit](() => runnable.run()).onComplete {
    case Success(_) =>
    case Failure(cause) => reportFailure(cause)
  }

  override def reportFailure(cause: Throwable): Unit = {
    logger.error("Failed executing on default worker executor", cause)
  }

}

object ThreadExecutorExecutionContext {

  def apply(vertx: Vertx): ThreadExecutorExecutionContext = new ThreadExecutorExecutionContext(vertx)
  
}
