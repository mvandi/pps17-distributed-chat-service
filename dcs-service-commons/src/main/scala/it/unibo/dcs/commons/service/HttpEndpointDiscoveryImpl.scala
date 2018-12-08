package it.unibo.dcs.commons.service

import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.{WebClient => JWebClient}
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.client.WebClient
import io.vertx.servicediscovery.types.HttpEndpoint
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.service.Constants.PUBLISH_CHANNEL
import it.unibo.dcs.commons.service.HttpEndpointDiscoveryImpl.RECORD_TYPE
import rx.lang.scala.Observable

final class HttpEndpointDiscoveryImpl(private[this] val discovery: ServiceDiscovery,
                                      private[this] val eventBus: EventBus) extends HttpEndpointDiscovery {

  override def getWebClient(name: String): Observable[WebClient] =
    getWebClientOrFail(name)
      .onErrorResumeNext[WebClient] {
      _ =>
        VertxHelper.toObservable[JWebClient, WebClient](WebClient(_)) { handler =>
          val consumer = eventBus.consumer[Record](PUBLISH_CHANNEL)
          consumer.handler { message =>
            val record = message.body
            if (record.getName == name && record.getType == RECORD_TYPE) {
              val webClient = discovery.getReference(record).getAs(classOf[JWebClient])
              handler(webClient)
              consumer.unregister()
            }
          }
        }
    }

  override def getWebClientOrFail(name: String): Observable[WebClient] =
    VertxHelper.toObservable[JWebClient, WebClient](WebClient(_)) { handler =>
      HttpEndpoint.getWebClient(
        discovery,
        new JsonObject()
          .put("name", name)
          .put("type", RECORD_TYPE),
        handler)
    }

}

object HttpEndpointDiscoveryImpl {

  private val RECORD_TYPE = HttpEndpoint.TYPE

}
