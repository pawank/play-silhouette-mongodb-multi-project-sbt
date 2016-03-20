package models.daos
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ ExpirableAuthenticator, LoginInfo, StorableAuthenticator }
import com.mohiva.play.silhouette.api.util.CacheLayer

import scala.concurrent.Future
import scala.concurrent.duration.{ Duration, FiniteDuration }
import scala.reflect.ClassTag
import javax.inject.Inject

import com.mohiva.play.silhouette.impl.authenticators.{ JWTAuthenticator, JWTAuthenticatorSettings }
import com.mohiva.play.silhouette.impl.daos.AuthenticatorDAO
import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

case class PersistentJWTAuthenticator(
  id: String,
  loginInfo: LoginInfo,
  lastUsedDateTime: DateTime,
  expirationDateTime: DateTime,
  idleTimeout: Option[FiniteDuration])
    extends StorableAuthenticator with ExpirableAuthenticator {

  /**
   * The Type of the generated value an authenticator will be serialized to.
   */
  override type Value = String
}

/**
 * Implementation of the authenticator DAO which uses the cache layer to persist the authenticator.
 *
 * @param db Mongodb
 * @tparam T The type of the authenticator to store.
 */
class AuthenticatorDbDAO @Inject() (db: DB)
    extends AuthenticatorDAO[JWTAuthenticator] {

  import play.api.libs.json.Reads._
  import play.api.libs.json.Writes._

  import scala.concurrent.duration._
  implicit object FiniteDurationFormat extends Format[FiniteDuration] {
    def reads(json: JsValue): JsResult[FiniteDuration] = LongReads.reads(json).map(_.seconds)
    def writes(o: FiniteDuration): JsValue = LongWrites.writes(o.toSeconds)
  }

  implicit val jWTAuthenticatorFormat = Json.format[PersistentJWTAuthenticator]

  def collection: JSONCollection = db.collection[JSONCollection]("auth_detail")
  /**
   * Finds the authenticator for the given ID.
   *
   * @param id The authenticator ID.
   * @return The found authenticator or None if no authenticator could be found for the given ID.
   */
  override def find(id: String): Future[Option[JWTAuthenticator]] = {
    val data: Future[Option[PersistentJWTAuthenticator]] = collection
      .find(Json.obj("id" -> id))
      .one[PersistentJWTAuthenticator]

    data.flatMap {
      case None => Future.successful(Option.empty[JWTAuthenticator])
      case Some(authenticator) =>
        val jWTAuthenticator = JWTAuthenticator(id = authenticator.id, loginInfo = authenticator.loginInfo,
          lastUsedDateTime = authenticator.lastUsedDateTime, expirationDateTime = authenticator.expirationDateTime,
          idleTimeout = authenticator.idleTimeout)
        Future(Some(jWTAuthenticator))
    }
  }

  /**
   * Adds a new authenticator.
   *
   * @param authenticator The authenticator to add.
   * @return The added authenticator.
   */
  override def add(authenticator: JWTAuthenticator): Future[JWTAuthenticator] = {
    val auth = PersistentJWTAuthenticator(id = authenticator.id, loginInfo = authenticator.loginInfo,
      lastUsedDateTime = authenticator.lastUsedDateTime, expirationDateTime = authenticator.expirationDateTime,
      idleTimeout = authenticator.idleTimeout)
    collection.insert(auth)
    Future.successful(authenticator)
  }

  /**
   * Updates an already existing authenticator.
   *
   * @param authenticator The authenticator to update.
   * @return The updated authenticator.
   */
  override def update(authenticator: JWTAuthenticator): Future[JWTAuthenticator] = {
    val auth = PersistentJWTAuthenticator(id = authenticator.id, loginInfo = authenticator.loginInfo,
      lastUsedDateTime = authenticator.lastUsedDateTime, expirationDateTime = authenticator.expirationDateTime,
      idleTimeout = authenticator.idleTimeout)
    collection.update(Json.obj("id" -> authenticator.id), auth)
    Future.successful(authenticator)
  }

  /**
   * Removes the authenticator for the given ID.
   *
   * @param id The authenticator ID.
   * @return An empty future.
   */
  override def remove(id: String): Future[Unit] = {
    collection.remove(Json.obj("id" -> id))
    Future.successful(() => Unit)
  }
}
