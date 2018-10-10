package it.unibo.dcs.service.user.repository

import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.request.{CreateUserRequest, GetUserRequest}
import rx.lang.scala.Observable

trait UserDataStore {

  def getUserByUsername(request: GetUserRequest): Observable[User]

  def createUser(request: CreateUserRequest): Observable[User]

}
