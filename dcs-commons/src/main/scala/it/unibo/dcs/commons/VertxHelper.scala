package it.unibo.dcs.commons

import io.vertx.core.eventbus.{MessageCodec, EventBus => JEventBus}
import io.vertx.core.{AsyncResult, Future, Handler, Context => JContext, Vertx => JVertx}
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.core.{Context, Vertx}
import it.unibo.dcs.commons.ReflectionHelper.asClassOf
import it.unibo.dcs.commons.RxHelper.Implicits.RxObservable
import it.unibo.dcs.commons.VertxHelper.Implicits.handlerToFunction
import rx.lang.scala.Observable

import scala.collection.mutable
import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

object VertxHelper {

  def toObservable[T](action: (AsyncResult[T] => Unit) => Unit): Observable[T] = toObservable[T, T](identity)(action)

  def toObservable[J, S](converter: J => S)(action: (AsyncResult[J] => Unit) => Unit): Observable[S] = {
    val javaObservable = io.vertx.rx.java.RxHelper.observableFuture[J]()
    action(javaObservable.toHandler())
    RxObservable[J](javaObservable).map[S](converter)
  }

  object Implicits {

    implicit def vertxToJVertx(vertx: Vertx): JVertx = vertx.asJava.asInstanceOf[JVertx]

    implicit def contextToJContext(context: Context): JContext = context.asJava.asInstanceOf[JContext]

    implicit def handlerToFunction[T](handler: Handler[T]): Function[T, Unit] = handler.handle

    implicit def functionToHandler[T](handler: Function[T, Any]): Handler[T] = (event: T) => handler(event)

    implicit def toSucceededFuture[T](result: T): AsyncResult[T] = Future.succeededFuture(result)

    implicit def toFailedFuture[T](cause: Throwable): AsyncResult[T] = Future.failedFuture(cause)

    implicit class RichAsyncResult[T](ar: AsyncResult[T]) {
      final def toTry: Try[T] =
        if (ar.succeeded)
          Success(ar.result)
        else if (ar.failed)
          Failure(ar.cause)
        else
          Failure(new IllegalStateException("Async result is neither succeeded or failed"))
    }

    implicit class RichEventBus(eventBus: EventBus) {

      private[this] lazy val asJava: JEventBus = eventBus.asJava.asInstanceOf[JEventBus]

      def registerCodec(codec: MessageCodec[_, _]): EventBus = {
        asJava.registerCodec(codec)
        eventBus
      }

      def unregisterCodec(name: String): EventBus = {
        asJava.unregisterCodec(name)
        eventBus
      }

      def registerDefaultCodec[T](codec: MessageCodec[T, _])(implicit ct: ClassTag[T]): EventBus = {
        asJava.registerDefaultCodec(asClassOf(ct), codec)
        eventBus
      }

      def unregisterDefaultCodec[T](implicit ct: ClassTag[T]): EventBus = {
        asJava.unregisterDefaultCodec(asClassOf(ct))
        eventBus
      }

      def address(name: String): Address =
        if (addresses contains name) {
          addresses(name)
        } else {
          val address = Address(eventBus, name)
          addresses(name) = address
          address
        }

    }

  }

  private val addresses = mutable.Map[String, Address]()


}
