package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetMessagesResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import rx.lang.scala.Subscriber

class GetMessagesSubscriber (protected override val response: HttpServerResponse)
  extends Subscriber[GetMessagesResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetMessagesResult): Unit = {
    log.debug(s"Retrieving result: $result")
    response endWith result
  }

}

object GetMessagesSubscriber {

  def apply(response: HttpServerResponse): GetMessagesSubscriber = {
    new GetMessagesSubscriber(response)
  }

}