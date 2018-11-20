package it.unibo.dcs.commons

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpMethod._
import io.vertx.lang.scala.json.{Json, JsonArray, JsonObject}
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.client.HttpResponse
import io.vertx.scala.ext.web.handler.CorsHandler
import io.vertx.scala.ext.web.{Router, RoutingContext}
import it.unibo.dcs.commons.JsonHelper.Implicits.{jsonArrayToString, jsonObjectToString}
import it.unibo.dcs.commons.VertxWebHelper.Implicits._
import org.apache.http.entity.ContentType

import scala.language.implicitConversions

object VertxWebHelper {

  def getParam(context: RoutingContext, paramName: String)(error: => Throwable): String =
    context.request().getParam(paramName).getOrElse(throw error)

  def getContextData(urlParameter: String)(implicit context: RoutingContext): Option[String] =
    context.request.getParam(urlParameter)

  def getJsonBodyData(fieldName: String)(implicit context: RoutingContext): Option[String] =
    context.getBody().map(body => body.toJsonObject.getValue(fieldName)).map(value => value.toString)

  def respondWithCode(statusCode: Int)(implicit context: RoutingContext): Unit =
    context.response.setStatusCode(statusCode).end

  def respond(status: HttpResponseStatus)(implicit context: RoutingContext): Unit = {
    context.response.setStatus(status).end()
  }

  def isCodeSuccessful(statusCode: Int): Boolean = statusCode / 100 == 2

  def doIfDefined(id: Option[_], action: => Unit)(implicit context: RoutingContext): Unit =
    if (id.isDefined) {
      action
    } else {
      respond(HttpResponseStatus.BAD_REQUEST)
    }

  def getTokenFromHeader(implicit context: RoutingContext): Option[String] = {
    context.request().headers().get(HttpHeaders.AUTHORIZATION.toString).map(token => token.split(" ").last)
  }

  def endErrorResponse(response: HttpServerResponse,
                       httpResponseStatus: HttpResponseStatus,
                       errorType: String, description: String): Unit = {
    val statusJson: JsonObject = httpResponseStatus
    val typeField = ("type", errorType)
    val descriptionField = ("description", description)
    response.setStatus(httpResponseStatus)
    response.end(Json.obj(("status", statusJson), typeField, descriptionField))
  }

  def responseStatus(response: HttpResponse[Buffer]): HttpResponseStatus = {
    HttpResponseStatus.valueOf(response.statusCode())
  }

  def setupCors(router: Router): Unit =
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

  object Implicits {

    implicit def httpResponseStatusToJsonObject(status: HttpResponseStatus): JsonObject =
      new JsonObject().put("code", status.code).put("reasonPhrase", status.reasonPhrase)

    implicit def contentTypeToString(contentType: ContentType): String = {
      contentType.getMimeType
    }

    implicit class RichHttpServerResponse(response: HttpServerResponse) {

      def setStatus(status: HttpResponseStatus): HttpServerResponse =
        response.setStatusCode(status.code).setStatusMessage(status.reasonPhrase)

      def endWith(json: JsonArray): Unit = response end json

      def endWith(json: JsonObject): Unit = response end json

    }

  }

}
