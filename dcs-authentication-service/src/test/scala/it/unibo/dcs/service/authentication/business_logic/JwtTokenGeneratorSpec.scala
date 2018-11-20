package it.unibo.dcs.service.authentication.business_logic

import java.util.Date

import io.vertx.scala.core.Vertx
import io.vertx.scala.ext.auth.jwt.JWTAuth
import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.authentication.server.setupHelpers.createJwtAuthOptions
import org.scalamock.scalatest.MockFactory

class JwtTokenGeneratorSpec extends JUnitSpec with MockFactory {

  private val username = "ale"
  private val tokenDecoder = JwtTokenDecoder()

  private val jwtAuth = JWTAuth.create(Vertx.vertx, createJwtAuthOptions())

  it should "return the correct user when the token gets decoded" in {
    val token = JwtTokenGenerator(jwtAuth, username)
    assert(tokenDecoder getUsernameFromToken token equals username)

    val issuedAtTimestamp = tokenDecoder getTokenIssuedAtTimestamp token
    val currentTimestamp = new Date().toInstant.toEpochMilli / 1000
    assert(issuedAtTimestamp > 0 && issuedAtTimestamp <= currentTimestamp)
  }
}