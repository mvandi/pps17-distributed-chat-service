package it.unibo.dcs.service.room.repository

import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

trait RoomRepository {

  def createUser(request: CreateUserRequest): Observable[Unit]

}
