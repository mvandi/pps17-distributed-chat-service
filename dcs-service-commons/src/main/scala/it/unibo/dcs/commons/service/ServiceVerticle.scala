package it.unibo.dcs.commons.service

import io.vertx.core.{AbstractVerticle, Context, Vertx}
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.core.http.{HttpServer, HttpServerOptions}
import io.vertx.scala.ext.web.Router
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.commons.VertxHelper.Implicits._
import it.unibo.dcs.commons.logging.Logging
import it.unibo.dcs.commons.service.ServiceVerticle._
import rx.lang.scala.Observable

abstract class ServiceVerticle extends ScalaVerticle with Logging {

  private[this] var _eventBus: EventBus = _
  private[this] var _router: Router = _

  override def init(jVertx: Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
    _eventBus = vertx.eventBus

    _router = Router.router(vertx)
    initializeRouter(_router)
  }

  protected[this] final def startHttpServer(host: String,
                                            port: Int,
                                            options: HttpServerOptions = defaultOptions): Observable[HttpServer] =
    VertxHelper.toObservable[HttpServer] {
      vertx.createHttpServer(options)
        .requestHandler(_router accept _)
        .listen(port, host, _)
    }

  protected[this] def initializeRouter(router: Router): Unit

  protected[this] final def eventBus: EventBus = _eventBus

}

object ServiceVerticle {

  def defaultOptions: HttpServerOptions = HttpServerOptions()

}
