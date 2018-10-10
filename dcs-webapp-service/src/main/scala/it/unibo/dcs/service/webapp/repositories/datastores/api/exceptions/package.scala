package it.unibo.dcs.service.webapp.repositories.datastores.api

package object exceptions {

  final case class RegistrationResponseException() extends RuntimeException

  final case class LoginResponseException() extends RuntimeException

  final case class GetUserResponseException() extends RuntimeException

  final case class UserCreationResponseException() extends RuntimeException

}
