package forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.i18n.Messages

import play.api.Play.current
import play.api.i18n.Messages.Implicits._

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  /**
   * A play framework form.
   */
  val formSignUp = Form[SignUpData](
    mapping(
      "company" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "emails" -> tuple(
        "email" -> email,
        "emailConfirmed" -> text
      ).verifying(Messages("emailConfirm.not.same"), emails => emails._1 == emails._2),
      "password" -> nonEmptyText
    )((company, firstName, lastName, emails, password) => SignUpData(company, firstName, lastName, emails._1, password)) //apply
    (data => Some((data.company, data.firstName, data.lastName, (data.email, data.email), data.password))) //unapply
  )

  /**
   * The form data.
   *
   * @param firstName The first name of a user.
   * @param lastName The last name of a user.
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class SignUpData(
    company: String,
    firstName: String,
    lastName: String,
    email: String,
    password: String)
}
