package it.unibo.dcs.commons

import java.nio.charset.StandardCharsets

import io.vertx.core.json.JsonObject
import io.vertx.core.{AsyncResult, Future, Handler, Context => JContext, Vertx => JVertx}
import io.vertx.scala.core.{Context, Vertx}
import it.unibo.dcs.commons.RxHelper.Implicits.RxObservable
import it.unibo.dcs.commons.VertxHelper.Implicits._
import org.apache.commons.io.IOUtils
import rx.lang.scala.Observable

import scala.util.{Failure, Success, Try}

object VertxHelper {

  def toObservable[T](action: (AsyncResult[T] => Unit) => Unit): Observable[T] = toObservable[T, T](identity)(action)

  def toObservable[J, S](converter: J => S)(action: (AsyncResult[J] => Unit) => Unit): Observable[S] = {
    val javaObservable = io.vertx.rx.java.RxHelper.observableFuture[J]()
    action(javaObservable.toHandler())
    RxObservable[J](javaObservable).map[S](converter)
  }

  def readJsonObject(file: String): JsonObject = {
    val in = getClass.getResourceAsStream(file)
    val str = IOUtils.toString(in, StandardCharsets.UTF_8)
    new JsonObject(str)
  }

  object Implicits {

    implicit def vertxToJVertx(vertx: Vertx): JVertx = vertx.asJava.asInstanceOf[JVertx]

    implicit def contextToJContext(context: Context): JContext = context.asJava.asInstanceOf[JContext]

    implicit def handlerToFunction[T](handler: Handler[T]): Function[T, Unit] = handler.handle

    implicit def functionToHandler[T](handler: Function[T, Any]): Handler[T] = (event: T) => handler(event)

    implicit def toSucceededFuture[T] (result: T): AsyncResult[T] = Future.succeededFuture(result)

    implicit def toFailedFuture[T] (causeFailure: Throwable): AsyncResult[T] = Future.failedFuture(causeFailure)

    implicit class RichAsyncResult[T](ar: AsyncResult[T]) {
      final def toTry: Try[T] =
        if (ar.succeeded)
          Success(ar.result)
        else if (ar.failed)
          Failure(ar.cause)
        else
          Failure(new IllegalStateException("Async result is neither succeeded or failed"))
    }

  }

}
