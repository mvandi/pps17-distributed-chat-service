package it.unibo.dcs.service.authentication

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.Json
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.RoutingContext
import it.unibo.dcs.commons.JsonHelper.Implicits.jsonObjectToString
import it.unibo.dcs.commons.VertxWebHelper.Implicits.RichHttpServerResponse
import it.unibo.dcs.commons.VertxWebHelper.respond
import it.unibo.dcs.exceptions.ErrorSubscriber
import it.unibo.dcs.service.authentication.interactor.usecases._
import it.unibo.dcs.service.authentication.request.Requests._
import rx.lang.scala.Subscriber

package object subscriber {

  class TokenSubscriber(protected override val response: HttpServerResponse, resultStatus: HttpResponseStatus)
    extends Subscriber[String] with ErrorSubscriber {

    override def onNext(token: String): Unit =
      response.setStatus(resultStatus).end(Json.obj(("token", token)))
  }

  class DeleteUserSubscriber(protected override val response: HttpServerResponse)
    extends Subscriber[Unit] with ErrorSubscriber {

    override def onCompleted(): Unit = response.setStatus(HttpResponseStatus.NO_CONTENT).end()
  }

  class OkSubscriber(protected override val response: HttpServerResponse)
    extends Subscriber[Unit] with ErrorSubscriber {

    override def onCompleted(): Unit = response.setStatus(HttpResponseStatus.OK).end()
  }


  class TokenCheckSubscriber(protected override val response: HttpServerResponse)
                            (implicit routingContext: RoutingContext)
    extends Subscriber[Boolean] with ErrorSubscriber {

    override def onNext(tokenIsValid: Boolean): Unit = {
      if (tokenIsValid) {
        respond(HttpResponseStatus.OK)
      } else {
        respond(HttpResponseStatus.UNAUTHORIZED)
      }
    }
  }

}
