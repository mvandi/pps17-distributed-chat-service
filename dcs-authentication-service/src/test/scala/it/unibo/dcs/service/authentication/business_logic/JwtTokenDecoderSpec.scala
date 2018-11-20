package it.unibo.dcs.service.authentication.business_logic

import it.unibo.dcs.commons.test.JUnitSpec
import org.scalamock.scalatest.MockFactory

class JwtTokenDecoderSpec extends JUnitSpec with MockFactory {

  private  val username = "ale"
  private  val token = "header.eyJzdWIiOiAiYWxlIn0=.signature"

  private  val tokenDecoder = JwtTokenDecoder()

  it should "return the correct user when the token gets decoded" in {
    assert(tokenDecoder getUsernameFromToken token equals username)
  }
}