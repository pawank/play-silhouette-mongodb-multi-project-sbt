// scalastyle:off
package controllers.admin

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.{ CookieAuthenticator, JWTAuthenticator }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.User
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.libs.mailer.MailerClient
import utils.responses.rest.Good

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param socialProviderRegistry The social provider registry.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  mailerClient: MailerClient)
    extends Silhouette[User, JWTAuthenticator] {

  import User.jsonFormat
  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Ok(Json.toJson(user)))
      case None => Future.successful(Ok(Json.toJson(Good(message = "you are not logged! Login man!"))))
    }
  }
  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def testApi = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Ok(Json.toJson(user)))
      case None => Future.successful(Ok(Json.toJson(Good(message = "you are not logged! Login man!"))))
    }
  }

}
// scalastyle:on
