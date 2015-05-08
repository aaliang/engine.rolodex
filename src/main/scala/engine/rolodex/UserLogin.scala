package engine.rolodex

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database.dynamicSession
import engine.BaseDAO

case class UserLogin(
  hash: String,
  username: String,
  email: Option[String],
  salt: String)

class UserLogins(tag: Tag) extends Table[UserLogin](tag, "user_login") {
  def hash = column[String]("hash")
  def username = column[String]("username")
  def email = column[String]("email")
  def salt = column[String]("salt")

  def * = (hash, username, email.?, salt) <> (UserLogin.tupled, UserLogin.unapply _)
}

object UserLoginDAO extends BaseDAO {

  private val userlogin = TableQuery[UserLogins]

  def selectAll = db withDynSession {
    userlogin.run
  }

  def insert(userLogin: UserLogin) = db withDynSession {
    userlogin.insert(userLogin)
  }

  def insert(hash: String, username: String, email: Option[String], salt: String) = db withDynSession {
    userlogin.insert((UserLogin(hash, username, email, salt)))
  }

  def insertAll(args: Seq[UserLogin]) = db withDynSession {
    userlogin ++= args
  }
}