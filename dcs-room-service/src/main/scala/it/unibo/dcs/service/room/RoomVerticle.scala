package it.unibo.dcs.service.room

import java.util.Date

import io.vertx.core.{AbstractVerticle, Context, Vertx => JVertx}
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.BodyHandler
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.VertxWebHelper.Implicits.contentTypeToString
import it.unibo.dcs.commons.VertxWebHelper.getParam
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.commons.{RxHelper, VertxWebHelper}
import it.unibo.dcs.exceptions.{RoomNameRequiredException, UsernameRequiredException}
import it.unibo.dcs.service.room.RoomVerticle.Implicits._
import it.unibo.dcs.service.room.interactor.usecases._
import it.unibo.dcs.service.room.interactor.validations._
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request._
import it.unibo.dcs.service.room.subscriber._
import it.unibo.dcs.service.room.validator._
import org.apache.http.entity.ContentType

import scala.language.implicitConversions

final class RoomVerticle(private[this] val roomRepository: RoomRepository, val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private[this] var host: String = _
  private[this] var port: Int = _

  override def init(jVertx: JVertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    host = config.getString("host")
    port = config.getInteger("port")
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())
    VertxWebHelper.setupCors(router)

    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))

    val createUserUseCase = {
      val validation = CreateUserValidation(threadExecutor, postExecutionThread, CreateUserValidator())
      CreateUserUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val getRoomsUseCase = {
      val validation = GetRoomsValidation(threadExecutor, postExecutionThread, GetRoomsValidator())
      GetRoomsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val createRoomUseCase = {
      val validation = CreateRoomValidation(threadExecutor, postExecutionThread, CreateRoomValidator())
      CreateRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val deleteRoomUseCase = {
      val validation = DeleteRoomValidation(threadExecutor, postExecutionThread, DeleteRoomValidator())
      DeleteRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val joinRoomUseCase = {
      val validation = JoinRoomValidation(threadExecutor, postExecutionThread, JoinRoomValidator())
      JoinRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    val sendMessageUseCase = {
    val validation = SendMessageValidation(threadExecutor, postExecutionThread, SendMessageValidator())
      SendMessageUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }

    val getMessagesUseCase = {
      val validation = GetMessagesValidation(threadExecutor, postExecutionThread, GetMessagesValidator())
      GetMessagesUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }

    router.post("/users")
      .consumes(ContentType.APPLICATION_JSON)
      .consumes(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateUserSubscriber(routingContext.response())
        createUserUseCase(request, subscriber)
      })

    val leaveRoomUseCase = {
      val validation = LeaveRoomValidation(threadExecutor, postExecutionThread, LeaveRoomValidator())
      LeaveRoomUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }

    val getRoomParticipationsUseCase = {
      val validation = GetRoomParticipationsValidation(threadExecutor, postExecutionThread, GetRoomParticipationsValidator())
      GetRoomParticipationsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }
    
    val getUserParticipationsUseCase = {
      val validation = GetUserParticipationsValidation(threadExecutor, postExecutionThread, GetUserParticipationsValidator())
      GetUserParticipationsUseCase(threadExecutor, postExecutionThread, roomRepository, validation)
    }

    router.post("/users")
      .consumes(ContentType.APPLICATION_JSON)
      .consumes(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateUserSubscriber(routingContext.response())
        createUserUseCase(request, subscriber)
      })
    

    router.post("/rooms")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateRoomSubscriber(routingContext.response())
        createRoomUseCase(request, subscriber)
      })

    router.delete("/rooms/:name")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {

        val roomName = getParam(routingContext, "name")(RoomNameRequiredException)
        val request: JsonObject = routingContext.getBodyAsJson.head.put("name", roomName)
        val subscriber = new DeleteRoomSubscriber(routingContext.response())
        deleteRoomUseCase(request, subscriber)
      })

    router.post("/rooms/:name/participations")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val roomName = getParam(routingContext, "name")(RoomNameRequiredException)
        val request = routingContext.getBodyAsJson.head.put("name", roomName)
        val subscriber = new JoinRoomSubscriber(routingContext.response())
        joinRoomUseCase(request, subscriber)
      })

    router.delete("/rooms/:name/participations/:username")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val roomName = routingContext.request().getParam("name").head
        val userName = routingContext.request().getParam("username").head
        val request = LeaveRoomRequest(roomName, userName)
        val subscriber = new LeaveRoomSubscriber(routingContext.response())
        leaveRoomUseCase(request, subscriber)
      })
        
    router.get("/rooms/:name/participations")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val roomName = routingContext.request().getParam("name").head
        val subscriber = new RoomParticipationsSubscriber(routingContext.response())
        getRoomParticipationsUseCase(GetRoomParticipationsRequest(roomName), subscriber)
      })

    router.get("/rooms")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val username = getParam(routingContext, "user")(UsernameRequiredException)
        val request = GetRoomsRequest(username)
        val subscriber = new GetRoomsSubscriber(routingContext.response())
        getRoomsUseCase(request, subscriber)
      })

    router.get("/rooms/:name/messages")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val roomName = routingContext.request().getParam("name").head
        val request = GetMessagesRequest(roomName)
        val subscriber = new GetMessagesSubscriber(routingContext.response())
        getMessagesUseCase(request, subscriber)
      })

    router.post("/rooms/:name/messages")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val roomName = routingContext.request().getParam("name").head
        val request = routingContext.getBodyAsJson().head.put("name", roomName)
        val subscriber = new SendMessageSubscriber(routingContext.response())
        sendMessageUseCase(request, subscriber)
      })

    router.post("/users")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson.head
        val subscriber = new CreateUserSubscriber(routingContext.response())
        createUserUseCase(request, subscriber)
      })

    router.get("/users/:username/participations")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val username = getParam(routingContext, "username")(UsernameRequiredException)
        val subscriber = new GetUserParticipationsSubscriber(routingContext.response())
        getUserParticipationsUseCase(GetUserParticipationsRequest(username), subscriber)
      })
  }

  override def start(): Unit = startHttpServer(host, port)
    .doOnCompleted(
      publisher.publish(name = "room-service", host = host, port = port)
        .subscribe(record => log.info(s"${record.getName} record published!"),
          log.error(s"Could not publish record", _)))
    .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
      log.error(s"Could not start server at http://$host:$port", _))

}

object RoomVerticle {

  object Implicits {

    implicit def jsonObjectToCreateUserRequest(json: JsonObject): CreateUserRequest =
      gson fromJsonObject[CreateUserRequest] json

    implicit def jsonObjectToCreateRoomRequest(json: JsonObject): CreateRoomRequest =
      gson fromJsonObject[CreateRoomRequest] json

    implicit def jsonObjectToDeleteRoomRequest(json: JsonObject): DeleteRoomRequest =
      gson fromJsonObject[DeleteRoomRequest] json

    implicit def jsonObjectToJoinRoomRequest(json: JsonObject): JoinRoomRequest =
      gson fromJsonObject[JoinRoomRequest] json

    implicit def jsonObjectToSendMessageRequest(json: JsonObject): SendMessageRequest =
      SendMessageRequest(json.getString("name"), json.getString("username"), json.getString("content"), new Date)
      
    implicit def jsonObjectToLeaveRoomRequest(json: JsonObject): LeaveRoomRequest =
      gson fromJsonObject[LeaveRoomRequest] json

  }

}