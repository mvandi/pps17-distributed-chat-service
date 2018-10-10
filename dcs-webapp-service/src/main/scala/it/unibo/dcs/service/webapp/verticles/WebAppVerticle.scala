package it.unibo.dcs.service.webapp.verticles

import io.vertx.core.eventbus.{EventBus => JEventBus}
import io.vertx.core.{AbstractVerticle, Context, Vertx}
import io.vertx.scala.core
import io.vertx.scala.core.eventbus.EventBus
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.servicediscovery.{Record, ServiceDiscovery}
import it.unibo.dcs.commons.service.codecs.RecordMessageCodec
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, HttpEndpointPublisherImpl, ServiceVerticle}
import it.unibo.dcs.service.webapp.verticles.requesthandler.ServiceRequestHandler


final class WebAppVerticle extends ServiceVerticle {

  private var host: String = _
  private var port: Int = _

  private var publisher: HttpEndpointPublisher = _
  private var requestHandler: ServiceRequestHandler = _

  override def init(jVertx: Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    initVerticleConfiguration(context)
    val discovery = ServiceDiscovery.create(jVertx)
    val eventBus: EventBus = initEventBus

    this.publisher = new HttpEndpointPublisherImpl(discovery, eventBus)
    this.requestHandler = ServiceRequestHandler(jVertx, eventBus)
  }

  private def initVerticleConfiguration(context: Context): Unit = {
    val config = context.config()
    this.host = config.getString("host")
    this.port = config.getInteger("port")
  }

  private def initEventBus = {
    val eventBus = vertx.eventBus
    eventBus.asJava.asInstanceOf[JEventBus].registerDefaultCodec(classOf[Record], new RecordMessageCodec())
    eventBus
  }

  override protected def initializeRouter(router: Router): Unit = {
    // To fetch request bodies
    router.route().handler(BodyHandler.create())
    val apiRouter = Router.router(vertx)

    implicit val ctx: core.Context = this.ctx

    apiRouter.post("/register")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleRegistration context)


    apiRouter.post("/login")
      .consumes("application/json")
      .produces("application/json")
      .handler(context => requestHandler handleLogin context)


    apiRouter.post("/protected/logout")
      .consumes("application/json")
      .handler(context => requestHandler handleLogout context)

    router.mountSubRouter("/api", apiRouter)
  }

  override def start(): Unit = {
    startHttpServer(host, port)
      .doOnCompleted(
        publisher.publish("webapp-service")
          .subscribe(_ => println("Record published!"),
            cause => println(s"Could not publish record: ${cause.getMessage}")))
      .subscribe(server => println(s"Server started at http://$host:${server.actualPort}"),
        cause => println(s"Could not start server at http://$host:$port: ${cause.getMessage}"))
  }

}


