package it.unibo.dcs.service.user

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.lang.scala.json.JsonObject

package object subscriber {
  object Implicits {
    implicit def httpResponseStatusToJsonObject(status: HttpResponseStatus): JsonObject = {
      new JsonObject().put("code", status.code()).put("reasonPhrase", status.reasonPhrase())
    }
  }
}
