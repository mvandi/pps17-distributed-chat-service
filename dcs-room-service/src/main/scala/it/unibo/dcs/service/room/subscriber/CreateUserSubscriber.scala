package it.unibo.dcs.service.room.subscriber

import io.vertx.scala.core.http.HttpServerResponse
import rx.lang.scala.Subscriber

final class CreateUserSubscriber(response: HttpServerResponse) extends Subscriber[Unit] {

  override def onNext(value: Unit): Unit = ()

  override def onCompleted(): Unit = response.end()

  override def onError(error: Throwable): Unit = ???

}
