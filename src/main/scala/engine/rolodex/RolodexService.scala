package engine.rolodex

import akka.actor.{ Props, Actor, ActorRef }
import akka.pattern.ask
import akka.util.Timeout
import spray.http.StatusCodes
import spray.http.StatusCodes._
import spray.http.CacheDirectives.`max-age`
import spray.http.HttpHeaders.`Cache-Control`
import spray.routing.HttpService
import spray.routing._
import spray.http._

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

trait RolodexService extends HttpService {

  implicit def ec = actorRefFactory.dispatcher

  def route(model: ActorRef)(implicit askTimeout: Timeout) = {
    path("user") {
      put {
        formFields('username, 'password, 'email.?).as(CreateUser) {
          (userCreds) =>
            onSuccess(model ? userCreds) {
              case _ => complete(OK, "k")
            }
        }
      }
    } ~
      pathPrefix("user" / Segment) { id =>
        get {
          onSuccess(model ? id) {
            case resp: String => complete(OK, resp)
          }
        } ~
          post {
            onSuccess(model ? id) {
              case resp: String => complete(OK, resp)
            }
          }
      } ~
      path("login") {
        post {
          formFields('username, 'password).as(LoginParameters) {
            (loginParameters) =>
              onSuccess(model ? loginParameters) {
                case resp: String =>
                  complete(OK, resp)
              }
          }
        }
      }
  }

}

//class RolodexService extends HttpService {
//  val route = {
//    get {
//      path("") {
//        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
//          complete(index)
//        }
//      }
//    }
//}