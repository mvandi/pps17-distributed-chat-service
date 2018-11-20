package it.unibo.dcs.service.authentication.repository

import java.util.Date

import it.unibo.dcs.exceptions._
import it.unibo.dcs.service.authentication.data.AuthenticationDataStore
import rx.lang.scala.Observable

class AuthenticationRepositoryImpl(private[this] val authDataStore: AuthenticationDataStore)
  extends AuthenticationRepository {

  override def createUser(username: String, password: String): Observable[Unit] =
    authDataStore.createUser(username, password)
      .onErrorResumeNext(_ => Observable.error(UserAlreadyExistsException(username)))

  override def checkUserCredentials(username: String, password: String): Observable[Unit] =
    authDataStore.checkUserCredentials(username, password)
      .onErrorResumeNext(_ => Observable.error(WrongUsernameOrPasswordException))

  override def invalidToken(token: String, tokenExpirationDate: Date): Observable[Unit] =
    authDataStore.invalidToken(token, tokenExpirationDate)

  override def isTokenValid(token: String): Observable[Boolean] =
    authDataStore.isTokenValid(token)
    .onErrorResumeNext(_ => Observable.error(InvalidTokenException))

  override def deleteUser(username: String, token: String): Observable[Unit] =
    authDataStore.deleteUser(username, token)
    .onErrorResumeNext(_ => Observable.error(UserNotFoundException(username)))
}

object AuthenticationRepositoryImpl {
  def apply(authDataStore: AuthenticationDataStore):AuthenticationRepositoryImpl =
    new AuthenticationRepositoryImpl(authDataStore)
}
