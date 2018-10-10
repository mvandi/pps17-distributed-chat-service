package it.unibo.dcs.authentication_service.business_logic

import io.vertx.lang.scala.json.Json
import io.vertx.scala.ext.auth.jwt.{JWTAuth, JWTOptions}

class JwtTokenGenerator(jwtAuth: JWTAuth) {

  def generate(username: String): String ={
    jwtAuth.generateToken(Json.obj(("sub", username), ("iat", System.currentTimeMillis())),
      JWTOptions().setExpiresInMinutes(1440)) //expires after one day
  }

}
