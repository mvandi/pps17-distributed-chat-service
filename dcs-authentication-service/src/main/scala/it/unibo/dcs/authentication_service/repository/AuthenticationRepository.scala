package it.unibo.dcs.authentication_service.repository

import java.time.LocalDateTime

import rx.lang.scala.Observable

trait AuthenticationRepository {

  def createUser(username: String, password: String): Observable[Unit]

  def loginUser(username: String, password: String): Observable[Unit]

  def invalidToken(token: String, tokenExpirationDate: LocalDateTime): Observable[Unit]

  def isTokenInvalid(token: String): Observable[Boolean]

}
