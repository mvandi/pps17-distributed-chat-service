package it.unibo.dcs.authentication_service.data

import java.time.LocalDateTime

import rx.lang.scala.Observable

trait AuthenticationDataStore {

  def createUser(username: String, password: String): Observable[Unit]

  def checkUserExistence(username: String, password: String): Observable[Unit]

  def invalidToken(token: String, expirationDate: LocalDateTime): Observable[Unit]

  def isTokenInvalid(token: String): Observable[Boolean]

}
