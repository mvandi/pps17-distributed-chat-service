package it.unibo.dcs.service.user.repository

import java.util.Date

import it.unibo.dcs.commons.test.JUnitSpec
import it.unibo.dcs.service.user.Mocks._
import it.unibo.dcs.service.user.model.User
import it.unibo.dcs.service.user.repository.impl.UserRepositoryImpl
import it.unibo.dcs.service.user.request.{CreateUserRequest, EditUserRequest, GetUserRequest}
import org.scalamock.scalatest.MockFactory
import org.scalatest.OneInstancePerTest
import rx.lang.scala.{Observable, Subscriber}

import scala.language.postfixOps

class UserRepositorySpec extends JUnitSpec with MockFactory with OneInstancePerTest {

  private val createUserRequest = CreateUserRequest("martynha", "Martina", "Magnani")
  private val getUserRequest = GetUserRequest(createUserRequest.username)
  private val editUserRequest = EditUserRequest("martynha", "Martina", "Magnani", "bio", visible = true)

  private val expectedUser = User(createUserRequest.username, createUserRequest.firstName,
    createUserRequest.lastName, editUserRequest.bio, visible = true, new Date())

  private val userRepository = new UserRepositoryImpl(userDataStore)

  private val subscriber = stub[Subscriber[User]]

  it should "create new user" in {
    //Given
    (userDataStore createUser _) expects createUserRequest returning (Observable just expectedUser)

    //When
    userRepository.createUser(createUserRequest).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedUser once()
    (() => subscriber onCompleted) verify() once()
  }

  it should "get user by username" in {
    //Given
    (userDataStore getUserByUsername _) expects getUserRequest returning (Observable just expectedUser)

    //When
    userRepository.getUserByUsername(getUserRequest).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedUser once()
    (() => subscriber onCompleted) verify() once()
  }

  it should "edit the specified profile" in {
    //Given
    (userDataStore editUser _) expects editUserRequest returning (Observable just expectedUser)

    //When
    userRepository.editUser(editUserRequest).subscribe(subscriber)

    //Then
    (subscriber onNext _) verify expectedUser once()
    (() => subscriber onCompleted) verify() once()
  }

}
