package it.unibo.dcs.service.user.model

package object exception {
  final case class UserNotFoundException(username: String) extends RuntimeException
}
