package engine

import slick.driver.PostgresDriver.backend.Database
import com.typesafe.config.ConfigFactory
import scala.language.postfixOps

/**
 * Generic DAO class. Concrete DAOs derive from me
 */
class BaseDAO {
  implicit val db = BaseDAO.db
}

object BaseDAO {
  private val conf = ConfigFactory.load()

  val db = Database.forConfig("mydb")
}