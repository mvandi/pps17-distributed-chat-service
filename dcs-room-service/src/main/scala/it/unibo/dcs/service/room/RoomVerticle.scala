package it.unibo.dcs.service.room

import io.vertx.core.{AbstractVerticle, Context, Vertx => JVertx}
import io.vertx.scala.ext.web.Router
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.service.room.interactor.CreateUserUseCase
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateUserRequest
import it.unibo.dcs.service.room.subscriber.CreateUserSubscriber

final class RoomVerticle(private[this] val roomRepository: RoomRepository, val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private var createUserUseCase: CreateUserUseCase = _

  private var host: String = _
  private var port: Int = _

  override def init(jVertx: JVertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
    val config = context.config
    host = config.getString("host")
    port = config.getInteger("port")

    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))
    createUserUseCase = new CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository)
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.post("/users")
      .consumes("application/json")
      .produces("application/json")
      .handler(routingContext => {
        val username = routingContext.getBodyAsJson.get.getString("username")
        val request = CreateUserRequest(username)
        createUserUseCase(request, new CreateUserSubscriber(routingContext.response()))
      })
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "room-service", host = host, port = port)
        .subscribe(_ => println("Record published!"),
                   cause => println(s"Could not publish record: ${cause.getMessage}")))
    .subscribe(server => println(s"Server started at http://$host:${server.actualPort}"),
               cause => println(s"Could not start server at http://$host:$port: ${cause.getMessage}"))

}
