package engine.authenticator

import shapeless.{ HNil }
import spray.http.{ IllegalRequestException, HttpHeader }
import spray.routing._
import spray.http.StatusCodes
import spray.http.StatusCodes.InternalServerError

/**
 * Provides a custom directive for authentication
 */
trait AuthHttpService extends HttpService {

  val invalidHeader = MalformedHeaderRejection("Invalid", s"Auth Token")

  case class AuthClaimSet(role: String, username: String)

  implicit def myExceptionHandler =
    ExceptionHandler {
      case a: IllegalRequestException =>
        complete(InternalServerError, "Bad Token")
      case _ =>
        complete(InternalServerError, "Bad Auth")
    }

  def fAuth (e:String) = {
    TokenAuthenticator.parseToken(e) match {
      case Some(deserializedToken) if deserializedToken.get("expires").get.toLong > System.currentTimeMillis =>
        provide(AuthClaimSet(deserializedToken.get("role").get, deserializedToken.get("username").get))
      case _ =>
        throw new IllegalRequestException(StatusCodes.Unauthorized)
    }
  }

  /**
   * Checks if the jwt token is (still) valid. Decoded claimset will be forwarded to inner route
   */
  def authEngine = {

    anyParams('token.?).flatMap {
      case Some(e) => fAuth(e)
      case _ => headerValueByName("X-Auth-Token").flatMap(fAuth)
    }
  }

  /**
   * only checks if token is (still) valid. does not forward jwt contents to inner routes
   * @deprecated
   */
  def blindAuth = {
    extract(_.request.headers.filter(_.name == "X-Auth-Token")).flatMap[HNil] {
      case Seq(authHeader: HttpHeader) => {
        TokenAuthenticator.parseToken(authHeader.value) match {
          case Some(e) =>
            e.get("expires") match {
              case Some(a: String) if a.toLong > System.currentTimeMillis => pass
              case _ => reject(MalformedHeaderRejection("Invalid", s"Auth Token Expired"))
            }
          case None => reject(invalidHeader)
        }
      }
      case _ => reject(invalidHeader)
    }
  }

}
