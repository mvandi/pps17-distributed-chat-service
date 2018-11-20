package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.webapp.interaction.Results.Implicits._
import it.unibo.dcs.service.webapp.interaction.Results.UserEditingResult
import rx.lang.scala.Subscriber

final class EditUserSubscriber (protected override val response: HttpServerResponse)
  extends Subscriber[UserEditingResult] with ErrorSubscriber with Logging {

  override def onNext(result: UserEditingResult): Unit = {
    val json: JsonObject = result
    log.debug(s"Retrieving result: $json")
    response end json.encode()
  }

}

object EditUserSubscriber {
  def apply(response: HttpServerResponse): EditUserSubscriber = {
    new EditUserSubscriber(response)
  }
}