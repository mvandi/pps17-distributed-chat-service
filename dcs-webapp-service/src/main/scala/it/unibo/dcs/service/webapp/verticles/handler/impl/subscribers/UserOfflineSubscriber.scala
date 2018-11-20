package it.unibo.dcs.service.webapp.verticles.handler.impl.subscribers

import io.vertx.lang.scala.json.JsonObject
import it.unibo.dcs.commons.Publisher
import it.unibo.dcs.service.webapp.interaction.Results.GetUserResult
import it.unibo.dcs.service.webapp.interaction.Results.Implicits.getUserToJsonObject
import rx.lang.scala.Subscriber

final class UserOfflineSubscriber(private[this] val publisher: Publisher) extends Subscriber[GetUserResult] {

  override def onNext(result: GetUserResult): Unit = publisher.publish[JsonObject](result)

}

object UserOfflineSubscriber {

  def apply(publisher: Publisher): UserOfflineSubscriber = new UserOfflineSubscriber(publisher)

}
