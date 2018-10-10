package it.unibo.dcs.service.room.data

import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

trait RoomDataStore {

  def createUser(request: CreateUserRequest): Observable[Unit]

}
