package utils

/**
 * Created by pawan on 19/03/16.
 */
object Utils {
  def makeCredentialIdentifier(company: String, email: String): String = company.replaceAll("""^[a-zA-Z0-9]+""", "") + ":" + email
}
