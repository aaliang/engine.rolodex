package engine

import akka.actor.{ ActorSystem }

object Boot extends App {

  implicit val system = ActorSystem("rolodex")
  system.actorOf(TopLevel.props, TopLevel.name)

}
