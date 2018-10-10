package it.unibo.dcs.service.user.repository.impl

import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.{UserDataStore, UserRepository}
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import rx.lang.scala.Observable

final class UserRepositoryImpl(private[this] val userDataStore: UserDataStore) extends UserRepository {

  override def createUser(request: CreateUserRequest): Observable[User] = userDataStore.createUser(request)

  override def getUserByUsername(request: GetUserRequest): Observable[User] = userDataStore.getUserByUsername(request)

}
