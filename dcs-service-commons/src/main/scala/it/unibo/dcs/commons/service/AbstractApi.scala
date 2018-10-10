package it.unibo.dcs.commons.service

import io.vertx.scala.ext.web.client.{HttpResponse, WebClient}
import rx.lang.scala.Observable

abstract class AbstractApi(private[this] val discovery: HttpEndpointDiscovery,
                           private[this] val serviceName: String) {

  private[this] var clientOption: Option[WebClient] = None

  discoverClient()

  private[this] def discoverClient(): Unit = {
    discovery.getWebClient(serviceName)
      .subscribe(c => clientOption = Some(c))
  }

  protected final def request[T](action: WebClient => Observable[HttpResponse[T]]): Observable[HttpResponse[T]] =
    clientOption match {
      case Some(c) => action(c)
        .doOnError { _ =>
            clientOption = None
            discoverClient()
        }
      // TODO: replace with a more meaningful exception
      case _ => throw new RuntimeException
    }

}
