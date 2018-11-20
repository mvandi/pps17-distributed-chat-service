package it.unibo.dcs.service.authentication

import it.unibo.dcs.commons.validation.{Conditions, Validator}
import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.authentication.business_logic.JwtTokenDecoder
import it.unibo.dcs.service.authentication.request.Requests._

package object validator {

  val jwtTokenDecoder = JwtTokenDecoder()

  object LogoutUserValidator {
    def apply(): Validator[LogoutUserRequest] = Validator[LogoutUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.token), TokenRequiredException)
        .addRule(request => checkUsernameInToken(request.token, request.username), InvalidTokenException)
    }
  }

  object DeleteUserValidator {
    def apply(): Validator[DeleteUserRequest] = Validator[DeleteUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => checkUsernameInToken(request.token, request.username), InvalidTokenException)
    }
  }

  object RegisterUserValidator {
    def apply(): Validator[RegisterUserRequest] = Validator[RegisterUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => Conditions.stringNotEmpty(request.password), PasswordRequiredException)
    }
  }

  object LoginUserValidator {
    def apply(): Validator[LoginUserRequest] = Validator[LoginUserRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.username), UsernameRequiredException)
        .addRule(request => Conditions.stringNotEmpty(request.password), PasswordRequiredException)
    }
  }

  object CheckTokenValidator {
    def apply: Validator[CheckTokenRequest] = Validator[CheckTokenRequest] {
      _
        .addRule(request => Conditions.stringNotEmpty(request.token), TokenRequiredException)
        .addRule(request => checkUsernameInToken(request.token, request.username),
          InvalidTokenException)
    }
  }

  private def checkUsernameInToken(token: String, username: String): Boolean =
    jwtTokenDecoder.getUsernameFromToken(token) equals username

}
