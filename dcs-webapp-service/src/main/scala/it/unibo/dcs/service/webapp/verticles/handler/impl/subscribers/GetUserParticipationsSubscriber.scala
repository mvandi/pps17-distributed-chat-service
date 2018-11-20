package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.GetUserParticipationsResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits.getUserParticipationsToJsonArray
import rx.lang.scala.Subscriber

final class GetUserParticipationsSubscriber(protected override val response: HttpServerResponse)
  extends Subscriber[GetUserParticipationsResult] with ErrorSubscriber with Logging {

  override def onNext(result: GetUserParticipationsResult): Unit = {
    log.debug(s"Retrieving result: $result")
    response endWith result
  }

}

object GetUserParticipationsSubscriber {
  def apply(response: HttpServerResponse): GetUserParticipationsSubscriber =
    new GetUserParticipationsSubscriber(response)
}

