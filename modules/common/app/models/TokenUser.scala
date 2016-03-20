package models

import java.util.UUID

import org.joda.time.DateTime

import scala.concurrent.Future

import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api._

case class TokenUser(id: String, company: String, email: String, expirationTime: DateTime, isSignUp: Boolean,
    firstName: String, lastName: String) extends Token {
  def isExpired: Boolean = expirationTime.isBeforeNow
}

object TokenUser {
  val hoursTillExpiry = 24

  implicit val passwordInfoFormat = Json.format[TokenUser]

  def newExpiryTime: DateTime = (new DateTime()).plusHours(TokenUser.hoursTillExpiry)

  def generateNewToken(email: String): TokenUser = TokenUser(UUID.randomUUID().toString, "", email, newExpiryTime, false, "", "")
}
