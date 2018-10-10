package it.unibo.dcs.service.webapp.repositories.impl

import it.unibo.dcs.service.webapp.repositories.AuthenticationRepository
import it.unibo.dcs.service.webapp.repositories.Requests.{LoginUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.repositories.datastores.AuthenticationDataStore
import rx.lang.scala.Observable


class AuthenticationRepositoryImpl(private val authenticationDataStore: AuthenticationDataStore)
  extends AuthenticationRepository {
  override def loginUser(loginUserRequest: LoginUserRequest): Observable[String] =
    authenticationDataStore.loginUser(loginUserRequest)

  override def registerUser(request: RegisterUserRequest): Observable[String] =
    authenticationDataStore.registerUser(request)

  override def logoutUser(username: String): Observable[Unit] = authenticationDataStore.logoutUser(username)
}
