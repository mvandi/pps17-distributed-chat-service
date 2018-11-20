package it.unibo.dcs.service.authentication.interactor.usecases

import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.service.authentication.business_logic.JwtTokenGenerator

/** It represents any use case that should return a jwt token */
trait ReturningTokenUseCase {

  /** Method that maps the unit result to a new token
    *
    * @param username the provided username
    * @param jwtAuth the jwt authentication provider
    * */
  def createToken(username: String, jwtAuth: JWTAuth): String =
    JwtTokenGenerator(jwtAuth, username)

}