package it.unibo.dcs.service.room

package object request {

  final case class CreateUserRequest(username: String) extends AnyVal

}
