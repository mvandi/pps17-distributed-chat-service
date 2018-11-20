package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.commons.VertxWebHelper.Implicits._
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.SendMessageResult
import rx.lang.scala.Subscriber

final class SendMessageSubscriber (protected override val response: HttpServerResponse,
                                   private[this] val publisher: Publisher)
  extends Subscriber[SendMessageResult] with ErrorSubscriber {

  override def onNext(result: SendMessageResult): Unit = {
    val res: JsonObject = result
    System.out.println(res)
    response.setStatus(HttpResponseStatus.CREATED).end(res.encode())
    publisher.publish(res)
  }

}

object SendMessageSubscriber {
  def apply(response: HttpServerResponse, publisher: Publisher): SendMessageSubscriber =
    new SendMessageSubscriber(response, publisher)
}
