package it.unibo.dcs.service.webapp.repositories.datastores.impl

import it.unibo.dcs.service.webapp.interaction.Requests.{EditUserRequest, RegisterUserRequest}
import it.unibo.dcs.service.webapp.model.User
import it.unibo.dcs.service.webapp.repositories.datastores.UserDataStore
import it.unibo.dcs.service.webapp.repositories.datastores.api.UserApi
import rx.lang.scala.Observable

class UserDataStoreNetwork(private val userApi: UserApi) extends UserDataStore {

  override def getUserByUsername(username: String): Observable[User] = userApi.getUserByUsername(username)

  override def createUser(request: RegisterUserRequest): Observable[User] =
    userApi.createUser(request)

  override def deleteUser(username: String): Observable[String] = userApi.deleteUser(username)

  override def editUser(request: EditUserRequest): Observable[User] = userApi.editUser(request)

  override def updateAccess(username: String): Observable[Unit] = userApi.updateAccess(username)
}
