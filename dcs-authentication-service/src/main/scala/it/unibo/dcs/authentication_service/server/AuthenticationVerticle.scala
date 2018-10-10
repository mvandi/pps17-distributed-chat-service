package it.unibo.dcs.authentication_service.server

import io.vertx.core
import io.vertx.core.{AbstractVerticle, Context}
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTAuthOptions}
import io.vertx.scala.ext.web.{Router, RoutingContext}
import io.vertx.scala.ext.web.handler.{BodyHandler, JWTAuthHandler}
import it.unibo.dcs.authentication_service.interactor.{LoginUserUseCase, LogoutUserUseCase, RegisterUserUseCase}
import it.unibo.dcs.authentication_service.repository.AuthenticationRepository
import it.unibo.dcs.commons.RxHelper
import it.unibo.dcs.commons.interactor.ThreadExecutorExecutionContext
import it.unibo.dcs.commons.interactor.executor.PostExecutionThread
import it.unibo.dcs.commons.service.ServiceVerticle
import it.unibo.dcs.commons.VertxWebHelper._
import rx.lang.scala.Subscriber

import scala.io.Source

class AuthenticationVerticle(authenticationRepository: AuthenticationRepository) extends ServiceVerticle {

  private var host: String = "127.0.0.1"
  private var port: Int = 8080 //random port

  override protected def initializeRouter(router: Router): Unit = {
    val authProvider = createJwtAuthProvider()
    router.route().handler(BodyHandler.create())
    setupRedirects(router, authProvider)
    setupRoutes(router, authProvider)
  }

  override def init(jVertx: core.Vertx, context: Context, verticle: AbstractVerticle): Unit = {
    super.init(jVertx, context, verticle)
  }

  override def start(): Unit = startHttpServer(host, port)

  private def createJwtAuthProvider(): JWTAuth = {
    val keyStoreSecret = Source.fromResource("keystore-secret.txt").getLines().next()
    val keyStoreOptions = Json.obj(("type", "jceks"), ("path", "keystore.jceks"), ("password", keyStoreSecret))
    val authOptions = JWTAuthOptions.fromJson(Json.obj(("keyStore", keyStoreOptions)))
    JWTAuth.create(vertx, authOptions)
  }

  private def setupRedirects(router: Router, authProvider: JWTAuth): Unit = {
    router.route("/protected/*").handler(JWTAuthHandler.create(authProvider))
    val filterRouter = Router.router(vertx)
    setupFilterRouter(filterRouter)
    filterRouter.mountSubRouter("/protected/", router)
  }

  private def setupFilterRouter(filterRouter: Router): Unit = {
    filterRouter.get.handler(context => {
      val token = getTokenFromHeader(context)
      if(token.isEmpty){
        respondWithCode(401)(context)
      }
      authenticationRepository.isTokenInvalid(token.get).subscribe(getTokenSubscriber(context))
    })
  }

  private def getTokenSubscriber(implicit context: RoutingContext): Subscriber[Boolean] = {
    new Subscriber[Boolean]() {
      override def onNext(value: Boolean): Unit = if (value) {
        context.next()
      } else {
        respondWithCode(401)
      }
      override def onError(error: Throwable): Unit = respondWithCode(401)
    }
  }

  private def setupRoutes(router: Router, jwtAuth: JWTAuth): Unit = {
    val requestHandler = getRequestHandler(jwtAuth)
    router.post("/register").handler(requestHandler.handleRegistration(_))
    router.post("/login").handler(requestHandler.handleLogin(_))
    router.post("/protected/logout").handler(requestHandler.handleLogout(_))
  }

  private def getRequestHandler(jwtAuth: JWTAuth): ServiceRequestHandler = {
    val threadExecutor = ThreadExecutorExecutionContext(vertx)
    val postExecutionThread = PostExecutionThread(RxHelper.scheduler(this.ctx))
    val loginUseCase = new LoginUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    val logoutUseCase = new LogoutUserUseCase(threadExecutor, postExecutionThread, authenticationRepository)
    val registerUseCase = new RegisterUserUseCase(threadExecutor, postExecutionThread, authenticationRepository, jwtAuth)
    new ServiceRequestHandlerImpl(loginUseCase, logoutUseCase, registerUseCase)
  }
}
