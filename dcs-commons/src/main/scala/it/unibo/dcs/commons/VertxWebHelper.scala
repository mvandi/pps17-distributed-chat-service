package it.unibo.dcs.commons

import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.web.RoutingContext

object VertxWebHelper {

  def getContextData(urlParameter: String)(implicit context: RoutingContext): Option[String] =
    context.request.getParam(urlParameter)

  def getJsonBodyData(fieldName: String)(implicit context: RoutingContext): Option[String] =
    context.getBody().map(body => body.toJsonObject.getValue(fieldName)).map(value => value.toString)

  def respondWithCode(statusCode: Int)(implicit context: RoutingContext): Unit =
    context.response.setStatusCode(statusCode).end

  def respond(statusCode: Int, errorMessage: String)(implicit context: RoutingContext): Unit = {
    val responseBody = Json.obj(("status", statusCode), ("message", errorMessage))
    context.response.setStatusCode(statusCode).end(responseBody.encodePrettily())
  }

  def respondOkToPostWithJson(body: JsonObject)(implicit context: RoutingContext): Unit = {
    context.response
      .putHeader("content-type", "application/json")
      .setStatusCode(201)
      .end(body.encodePrettily())
  }

  def isCodeSuccessful(statusCode: Int): Boolean = statusCode / 100 == 2

  def doIfDefined(id: Option[_], action: => Unit)(implicit context: RoutingContext): Unit =
    if (id.isDefined) {
      action
    } else respondWithCode(400)

  def getTokenFromHeader(implicit context: RoutingContext): Option[String] = {
    context.request().headers().get(HttpHeaders.AUTHORIZATION.toString).map(token => token.split(" ").last)
  }
}
