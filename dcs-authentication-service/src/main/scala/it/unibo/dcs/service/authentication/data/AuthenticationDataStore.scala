package it.unibo.dcs.service.authentication.data

import java.util.Date

import rx.lang.scala.Observable

/** Structure that allows access to authentication info by many possible different means
  * (e.g. network, file, database, etc) */
trait AuthenticationDataStore {

  /**
    * It saves user info and generates a new token
    *
    * @param username the username of the user
    * @param password the password of the user
    * @return an empty observable stream */
  def createUser(username: String, password: String): Observable[Unit]

  /**
    * It deletes all user info
    *
    * @param username the username of the user
    * @param token the jwt token of the user
    * @return an empty observable stream */
  def deleteUser(username: String, token: String): Observable[Unit]

  /**
    * It checks that the provided user credentials are correct
    *
    * @param username the username of the user
    * @param password the password of the user
    * @return an empty observable stream */
  def checkUserCredentials(username: String, password: String): Observable[Unit]

  /**
    * It adds the provided token to the set of invalid tokens
    *
    * @param token          the jwt token of the user
    * @param expirationDate the jwt token's expiration date
    * @return an empty observable stream */
  def invalidToken(token: String, expirationDate: Date): Observable[Unit]

  /**
    * It checks that the provided token is not in the set of invalid tokens
    *
    * @param token the jwt token of the user
    * @return an observable stream of just the check result, describing if the token is valid or not*/
  def isTokenValid(token: String): Observable[Boolean]

}
