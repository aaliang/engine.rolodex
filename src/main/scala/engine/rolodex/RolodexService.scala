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

    path("user" / Segment) { id =>
      get {
        onSuccess(model ? id) {
          case resp: String =>
            complete(OK, resp)
        }
      } ~
        post {
          onSuccess(model ? id) {
            case resp: String =>
              complete(OK, resp)
          }
        }
    } ~
      path("login") {
        post {
          parameters('username.as[String], 'password.as[String]).as(LoginParameters) {
            (loginParameters) =>
              complete(OK, "ok")
          }
        }
      }

  }

}