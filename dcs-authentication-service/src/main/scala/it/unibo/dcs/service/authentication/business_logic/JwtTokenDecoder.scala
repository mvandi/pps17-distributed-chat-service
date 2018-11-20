package it.unibo.dcs.service.authentication.business_logic

import io.vertx.lang.scala.json.{Json, JsonObject}
import org.apache.commons.codec.binary.Base64

/** Utility class useful to decode any jwt token, to get its payload */
class JwtTokenDecoder() {

  private val base64 = new Base64(true)

  /**
    * It decodes a token, obtaining the username present in the token's payload
    *
    * @param token the jwt token
    * @return a username */
  def getUsernameFromToken(token: String): String =
    getTokenBody(token).getString("sub")

  /**
    * It decodes a token, obtaining the "issued at" timestamp
    *
    * @param token the jwt token
    * @return a username */
  def getTokenIssuedAtTimestamp(token: String): Int =
    getTokenBody(token).getInteger("iat")

  private def getTokenBody(token: String): JsonObject = {
    val base64EncodedBody = token.split("\\.")(1)
    val jsonStringBody = new String(base64.decode(base64EncodedBody), "UTF-8")
    Json.fromObjectString(jsonStringBody)
  }

}

object JwtTokenDecoder {

  /** Factory method to instantiate the class
    *
    * @return an instantiation of the class */
  def apply(): JwtTokenDecoder = new JwtTokenDecoder()
}
