package it.unibo.dcs.service.authentication.repository

import java.util.Date

import rx.lang.scala.Observable

/** Structure that handles authentication data access and storage. */
trait AuthenticationRepository {

  /** @param username
    * Username needed to create the user
    * @param password
    * * User's password needed to create the user
    * @return an empty observable stream */
  def createUser(username: String, password: String): Observable[Unit]

  /** @param username
    * Username needed to delete the user
    * @param token
    * * The user's jwt token
    * @return an empty observable stream */
  def deleteUser(username: String, token: String): Observable[Unit]

  /** @param username
    * Username needed to check the credentials
    * @param password
    * * User's password needed to check the credentials
    * @return an empty observable stream */
  def checkUserCredentials(username: String, password: String): Observable[Unit]

  /** @param token
    * The user's jwt token
    * @param tokenExpirationDate
    * * The jwt token expiration date
    * @return an empty observable stream */
  def invalidToken(token: String, tokenExpirationDate: Date): Observable[Unit]

  /** @param token
    * The user's jwt token
    * @return an observable stream of just one boolean result, describing the token validity */
  def isTokenValid(token: String): Observable[Boolean]

}
