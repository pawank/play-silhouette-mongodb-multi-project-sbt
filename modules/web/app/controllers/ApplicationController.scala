// scalastyle:off
package controllers.web

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import forms._
import models.User
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.libs.mailer.MailerClient
import play.api.mvc.Action
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
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  mailerClient: MailerClient)
    extends Silhouette[User, CookieAuthenticator] {

  def index = Action { implicit request =>
    Ok(views.html.web.index())
  }

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def home = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.web.home(request.identity)))
  }

  /**
   * Handles the Sign In action.
   *
   * @return The result to display.
   */
  def signIn = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.home()))
      case None => Future.successful(Ok(views.html.web.signIn(SignInForm.form, socialProviderRegistry)))
      case unknown => Future.failed(new RuntimeException(s"request.identity returned an unexpected type $unknown"))
    }
  }

  /**
   * Handles the Sign Out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.index())
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))

    env.authenticatorService.discard(request.authenticator, result)
  }

  import User.jsonFormat
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
