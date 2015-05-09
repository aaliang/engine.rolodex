package engine

import slick.driver.PostgresDriver.backend.Database
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import akka.util.Timeout
import scala.language.postfixOps

class BaseDAO {
  implicit val db = BaseDAO.db

  implicit val timeout = Timeout(5 seconds) // needed for `?` below
}

object BaseDAO {
  private val conf = ConfigFactory.load()

  val db = Database.forConfig("mydb")
}