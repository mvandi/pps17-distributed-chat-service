package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits.logoutResultToJsonObject
import it.unibo.dcs.service.webapp.interaction.Results.LogoutResult
import rx.lang.scala.Subscriber

import scala.language.postfixOps

final class LogoutUserSubscriber(protected override val response: HttpServerResponse,
                                 private[this] val publisher: Publisher) extends Subscriber[LogoutResult]
  with ErrorSubscriber {

  override def onNext(result: LogoutResult): Unit = {
    val json: JsonObject = result
    response end()
    publisher publish[JsonObject] json
  }

}

object LogoutUserSubscriber {

  def apply(response: HttpServerResponse, publisher: Publisher): LogoutUserSubscriber = new LogoutUserSubscriber(response, publisher)
}
