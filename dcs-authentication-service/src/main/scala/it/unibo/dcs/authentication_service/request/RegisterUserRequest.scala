package it.unibo.dcs.authentication_service.request

final case class RegisterUserRequest(username: String, password: String) extends TokenRequest
