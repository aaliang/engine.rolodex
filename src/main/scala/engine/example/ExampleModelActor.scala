package engine.example

import akka.actor.{ Props, Actor }

object ExampleModelActor {
  def props: Props = Props[ExampleModelActor]
  def name = "model"

  case object ItemNotFound
  case class ItemSummaries(items: Seq[ItemSummary])
}

class ExampleModelActor extends Actor with ExampleModel {
  import ExampleModelActor._

  def receive = {
    case id: Int =>
      sender ! get(id).getOrElse(ItemNotFound)

    case 'list =>
      sender ! ItemSummaries(list)

    case ('query, term: String) =>
      sender ! ItemSummaries(query(term))

  }

}

