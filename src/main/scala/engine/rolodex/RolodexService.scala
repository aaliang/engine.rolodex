package engine.rolodex

import akka.actor.{ Props, Actor, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import spray.http.StatusCodes._
import com.typesafe.config.ConfigFactory
import engine.authenticator._

object RolodexServiceActor {

  def props(model: ActorRef)(implicit askTimeout: Timeout): Props = {
    Props(classOf[RolodexServiceActor], model, askTimeout)
  }

  def name = "rolodex-service"
}

class RolodexServiceActor(model: ActorRef, implicit val askTimeout: Timeout) extends Actor with RolodexService {

  def actorRefFactory = context

  def receive = runRoute(route(model))

}

trait RolodexService extends AuthHttpService {
  private val conf = ConfigFactory.load()

  val defaultTokenTTL = conf.getInt("rolodex-app.token-ttl")

  implicit val secretKey = conf.getString("rolodex-app.secret-key")

  implicit def ec = actorRefFactory.dispatcher

  def route(model: ActorRef)(implicit askTimeout: Timeout) = {
    path("user") {
      put {
        formFields('username, 'password, 'email.?, 'role.?).as(CreateUser) {
          (userCreds) =>
            onSuccess(model ? userCreds) {
              case resp: String => complete(OK, resp)
            }
        }
      }
    } ~
      path("login") {
        post {
          formFields('username, 'password).as(LoginParameters) {
            (loginParameters) =>
              onSuccess(model ? loginParameters) {
                case goodLogin: UserLogin =>
                  complete(OK, TokenAuthenticator.getToken(
                    Map(
                      "role" -> goodLogin.role.getOrElse(0).toString,
                      "username" -> goodLogin.username,
                      "expires" -> (System.currentTimeMillis + defaultTokenTTL).toString
                    )
                  ))
                case resp: String =>
                  complete(OK, resp)
              }
          }
        }
      } ~
      path("protected") {
        authEngine { (role, username) =>
          get {
            complete(OK, username) //for now, echo
          }
        }
      }
    /* ~ //redacted for now
    pathPrefix("user" / Segment / "password") { userid =>
      post {

        onSuccess(model ? userid) {
          case resp: String => complete(OK, resp)
        }
      }
    }*/
  }
}