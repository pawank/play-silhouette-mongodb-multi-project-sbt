package models.daos

import java.util.UUID
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDAOImpl._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._
import reactivemongo.api._

import scala.collection.mutable
import scala.concurrent.Future

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (db: DB) extends UserDAO {
  def collection: JSONCollection = db.collection[JSONCollection]("user")

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] = {
    collection.find(Json.obj("loginInfo" -> loginInfo)).one[User]
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] = {
    collection.find(Json.obj("userID" -> userID)).one[User]
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] = {
    collection.insert(user)
    Future.successful(user)
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {
}
