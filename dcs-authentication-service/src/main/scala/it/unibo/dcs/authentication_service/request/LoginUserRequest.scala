package it.unibo.dcs.authentication_service.request

final case class LoginUserRequest(username: String, password: String) extends TokenRequest
