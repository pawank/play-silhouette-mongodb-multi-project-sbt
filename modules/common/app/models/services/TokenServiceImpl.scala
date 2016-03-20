package models.services

import javax.inject.Inject

import models.TokenUser
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

import scala.concurrent.Future

class TokenServiceImpl @Inject() (db: DB) extends TokenService[TokenUser] {
  implicit val tokenFormat = Json.format[TokenUser]

  def collection: JSONCollection = db.collection[JSONCollection]("token")

  def create(token: TokenUser): Future[Option[TokenUser]] = {
    collection.insert(token).map { lastError =>
      println(s"Successfully inserted with LastError: $lastError")
      Some(token)
    }.recover({
      case e: Throwable =>
        println("Error in saving token:" + e.getMessage)
        e.printStackTrace()
        Option.empty[TokenUser]
    })
  }

  def retrieve(id: String): Future[Option[TokenUser]] = {
    collection.find(Json.obj("id" -> id)).one[TokenUser]
  }
  def consume(id: String): Unit = {
    collection.remove(Json.obj("id" -> id))
  }

  def findByEmail(email: String): Future[Option[TokenUser]] = {
    collection.find(Json.obj("email" -> email)).one[TokenUser]
  }
}
