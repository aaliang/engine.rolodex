package engine.rolodex

import akka.actor.{ Props, Actor }

object RolodexModelActor {
  def props: Props = Props[RolodexModelActor]

  def name = "rolodex-model"

  case object UserNotFound
  case class UserSummary(user: UserSummary)
}

class RolodexModelActor extends Actor {
  import RolodexModelActor._

  def receive = {
    case id: String =>
      sender ! id //for now echo
  }
}
