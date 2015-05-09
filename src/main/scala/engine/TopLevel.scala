
package engine

import engine.rolodex._
import akka.actor._
import spray.can.Http
import akka.io.IO
import akka.actor.Terminated
import akka.actor.SupervisorStrategy.{ Restart, Stop }
import akka.util.Timeout

object TopLevel {
  def props: Props = Props[ProductionTopLevel]
  def name = "top-level"
}

class ProductionTopLevel extends TopLevel with TopLevelConfig {
  private def c = context.system.settings.config
  def interface = c.getString("rolodex-app.service.interface")
  def port = c.getInt("rolodex-app.service.port")
  implicit def askTimeout = Timeout(c.getInt("rolodex-app.service.ask-timeout"), java.util.concurrent.TimeUnit.MILLISECONDS)

  def createModel = context.actorOf(RolodexModelActor.props, RolodexModelActor.name)
  def createService(model: ActorRef) = context.actorOf(RolodexServiceActor.props(model), RolodexServiceActor.name)
}

trait TopLevelConfig {
  def createModel: ActorRef
  def createService(model: ActorRef): ActorRef
  def interface: String
  def port: Int
}

class TopLevel extends Actor with ActorLogging {
  this: TopLevelConfig =>

  val model = createModel
  context watch model

  val service = createService(model)
  context watch service

  import context._
  IO(Http) ! Http.Bind(service, interface, port)

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _ if model == sender => Stop
    case _ if service == sender => Restart
  }

  def receive = {
    case Http.CommandFailed(_) => context stop self
    case Terminated(`model`) => context stop self
    case _ =>
  }

}