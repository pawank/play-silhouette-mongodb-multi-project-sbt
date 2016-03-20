// scalastyle:off
package controllers.admin

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.authenticators.{ JWTAuthenticator }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models.User
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{ Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import utils.responses.rest.Bad
import security.models.Token

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * The credentials auth controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param credentialsProvider The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param configuration The Play configuration.
 * @param clock The clock instance.
 */
class RestCredentialsAuthController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, JWTAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock)
    extends Silhouette[User, JWTAuthenticator] {

  implicit val restCredentialFormat = security.formatters.json.CredentialFormat.restFormat

  def authenticate2 = Action.async(parse.json) { implicit request =>
    request.body.validate[Credentials].map { data =>
      credentialsProvider.authenticate(data).flatMap { loginInfo =>
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => env.authenticatorService.create(loginInfo).map {
            case authenticator => authenticator
          }.flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            env.authenticatorService.init(authenticator).map { token =>
              Ok(Json.toJson(Token(token, authenticator.expirationDateTime)))
            }
          }
          case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
        }
      }.recover {
        case e: ProviderException =>
          Unauthorized(Json.toJson(Bad(code = Some(400), message = Messages("invalid.credentials"))))
      }
    }.recoverTotal {
      case error =>
        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.credentials"))))
    }
  }
  /**
   * Authenticates a user against the credentials provider.
   *
   * @return Token JSON
   */
  def authenticate = Action.async(parse.json) { implicit request =>
    val credentials = request.body.as[Credentials]
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      val result = Redirect(routes.ApplicationController.index())
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          val c = configuration.underlying
          env.authenticatorService.create(loginInfo).flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, request, request2Messages))
            env.authenticatorService.init(authenticator).flatMap { token =>
              println("Token found:" + token)
              val r = Ok(Json.toJson(Token(token, authenticator.expirationDateTime)))
              env.authenticatorService.embed(token, r)
              Future.successful(r)
            }
          }
        case None =>
          Future.successful(Ok(Json.toJson(Bad(code = Some(400), message = Messages("user.not.found")))))
        case unknown =>
          Future.successful(Ok(Json.toJson(Bad(code = Some(400), message = Messages("unknown.user")))))
      }
    }.recover {
      case e: ProviderException =>
        Unauthorized(Json.toJson(Bad(code = Some(400), message = Messages("invalid.credentials"))))
    }
  }
}
// scalastyle:on
