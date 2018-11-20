package it.unibo.dcs.service.authentication.business_logic

import java.util.Date

import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTOptions}

/** Utility object useful to generate jwt tokens */
object JwtTokenGenerator extends ((JWTAuth, String) => String){

  /**
    * It generates a token, given a username
    *
    * @param jwtAuth the vertx jwt authentication provider
    * @param username the username of the user
    * @return a jwt token */
  def apply(jwtAuth: JWTAuth, username: String): String = {
    val MAXIMUM_TOKEN_AGE = 300 // in minutes, it is equal to 5 hours
    jwtAuth.generateToken(Json.obj(("sub", username), ("iat", new Date().toInstant.toEpochMilli / 1000)),
      JWTOptions().setExpiresInMinutes(MAXIMUM_TOKEN_AGE)) //expires after 5 hours
  }

}