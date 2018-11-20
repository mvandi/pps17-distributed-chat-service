package it.unibo.dcs.service.user

import io.vertx.core.http.HttpMethod._
import io.vertx.core.{AbstractVerticle, Context => JContext, Vertx => JVertx}
import io.vertx.lang.scala.json.{Json, JsonObject}
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.handler.{BodyHandler, CorsHandler}
import it.unibo.dcs.commons.JsonHelper.Implicits.RichGson
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.VertxWebHelper.Implicits.contentTypeToString
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.{PostExecutionThread, ThreadExecutor}
import it.unibo.dcs.commons.service.{HttpEndpointPublisher, ServiceVerticle}
import it.unibo.dcs.commons.validation.Validator
import it.unibo.dcs.service.user.UserVerticle.Implicits._
import it.unibo.dcs.service.user.interactor.usecases._
import it.unibo.dcs.service.user.interactor.validations.{ValidateUserCreation, ValidateUserEditing}
import it.unibo.dcs.service.user.repository.UserRepository
import it.unibo.dcs.service.user.request.{CreateUserRequest, EditUserRequest, GetUserRequest}
import it.unibo.dcs.service.user.subscriber._
import it.unibo.dcs.service.user.validator.{UserCreationValidator, UserEditingValidator}
import org.apache.http.entity.ContentType

import scala.language.implicitConversions

final class UserVerticle(private[this] val userRepository: UserRepository,
                         private[this] val publisher: HttpEndpointPublisher) extends ServiceVerticle {

  private var host: String = _
  private var port: Int = _

  override def init(jVertx: JVertx, context: JContext, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)

    host = config getString "host"
    port = config getInteger "port"

  }

  override def start(): Unit = {
    startHttpServer(host, port)
      .doOnCompleted(
        publisher.publish(name = "user-service", host = host, port = port)
          .subscribe(record => log.info(s"${record.getName} record published!"),
            log.error(s"Could not publish record", _)))
      .subscribe(server => log.info(s"Server started at http://$host:${server.actualPort}"),
        log.error(s"Could not start server at http://$host:$port", _))
  }

  override protected def initializeRouter(router: Router): Unit = {
    router.route().handler(BodyHandler.create())

    router.route().handler(CorsHandler.create("*")
      .allowedMethod(GET)
      .allowedMethod(POST)
      .allowedMethod(PATCH)
      .allowedMethod(PUT)
      .allowedMethod(DELETE)
      .allowedHeader("Access-Control-Allow-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Content-Type"))

    val threadExecutor: ThreadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread: PostExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))

    val getUserUseCase = GetUserUseCase(threadExecutor, postExecutionThread, userRepository)

    val createUserUseCase = {
      val validator: Validator[CreateUserRequest] = UserCreationValidator(userRepository)
      val validation = ValidateUserCreation(threadExecutor, postExecutionThread, validator)
      CreateUserUseCase(threadExecutor, postExecutionThread, userRepository, validation)
    }

    val editUserUseCase = {
      val validator: Validator[EditUserRequest] = UserEditingValidator(userRepository)
      val validation = ValidateUserEditing(threadExecutor, postExecutionThread, validator)
      EditUserUseCase(threadExecutor, postExecutionThread, userRepository, validation)
    }

    val updateUserAccessUseCase = new UpdateUserAccessUseCase(threadExecutor, postExecutionThread, userRepository)

    router.get("/users/:username")
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val username = routingContext.request().getParam("username").head
        val request = Json.obj(("username", username))
        val subscriber = new GetUserSubscriber(routingContext.response())
        getUserUseCase(request, subscriber)
      })

    router.put("/users/:username")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val body = routingContext.getBodyAsJson().head
        val username = routingContext.request().getParam("username").head
        val firstName = body.getString("firstName")
        val lastName = body.getString("lastName")
        val bio = body.getString("bio")
        val visible = body.getBoolean("visible")
        val request = EditUserRequest(username, firstName, lastName, bio, visible)
        val subscriber = new EditUserSubscriber(routingContext.response())
        editUserUseCase(request, subscriber)
      })

    router.post("/users")
      .consumes(ContentType.APPLICATION_JSON)
      .produces(ContentType.APPLICATION_JSON)
      .handler(routingContext => {
        val request = routingContext.getBodyAsJson().head
        log.debug(s"Received request: $request")
        val subscriber = new CreateUserSubscriber(routingContext.response())
        createUserUseCase(request, subscriber)
      })

    router.put("/users/:username/access")
      .handler(routingContext => {
        val username = routingContext.request().getParam("username").head
        val subscriber = new UpdateUserAccessSubscriber(routingContext.response())
        updateUserAccessUseCase(username, subscriber)
      })
  }

}

object UserVerticle {

  object Implicits {

    implicit def jsonObjectToCreateUserRequest(json: JsonObject): CreateUserRequest =
      gson fromJsonObject[CreateUserRequest] json

    implicit def jsonObjectToGetUserRequest(json: JsonObject): GetUserRequest =
      gson fromJsonObject[GetUserRequest] json

  }

}
