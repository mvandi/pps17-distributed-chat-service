package it.unibo.dcs.authentication_service.request

trait TokenRequest {

  def username: String
  def password: String

}
