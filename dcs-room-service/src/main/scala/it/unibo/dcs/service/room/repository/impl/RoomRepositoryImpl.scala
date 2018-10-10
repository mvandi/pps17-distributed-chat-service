package it.unibo.dcs.service.room.repository.impl

import it.unibo.dcs.service.room.data.RoomDataStore
import it.unibo.dcs.service.room.repository.RoomRepository
import it.unibo.dcs.service.room.request.CreateUserRequest
import rx.lang.scala.Observable

final class RoomRepositoryImpl(private[this] val roomDataStore: RoomDataStore) extends RoomRepository {

  override def createUser(request: CreateUserRequest): Observable[Unit] = roomDataStore.createUser(request)

}
