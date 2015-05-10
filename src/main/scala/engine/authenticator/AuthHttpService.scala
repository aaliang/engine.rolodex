package engine.authenticator

import shapeless.{HNil }
import spray.http.{IllegalRequestException, HttpHeader }
import spray.routing._
import spray.http.StatusCodes
import spray.http.StatusCodes.InternalServerError

import scala.util.control.NonFatal

/**
 * Provides a custom directive for authentication
 */
trait AuthHttpService extends HttpService {

  val invalidHeader = MalformedHeaderRejection("Invalid", s"Auth Token")


  implicit def myExceptionHandler =
    ExceptionHandler {
      case a: IllegalRequestException =>
        complete(InternalServerError, "Bad Token")
    }

  /**
   * Checks if the jwt token is (still) valid. Decoded claimset will be forwarded to inner route
   */
  def authEngine = {

    headerValueByName("X-Auth-Token").flatMap(e =>
      TokenAuthenticator.parseToken(e) match {
        case Some(deserializedToken) if deserializedToken.get("expires").get.toLong > System.currentTimeMillis =>
          hprovide(deserializedToken :: HNil)
        case _ => {
          throw new IllegalRequestException(StatusCodes.Unauthorized)
          //this is a hack. can't figure out a better typesafe way to fail a request while passing contents
          hprovide("illegal" :: HNil)
        }
      }
    )
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