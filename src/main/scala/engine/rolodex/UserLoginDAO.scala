package engine.rolodex

import slick.driver.PostgresDriver.api._

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

  private val userloginTQ = TableQuery[UserLogins]

  def selectByUsername(username: String) = {
    db.run(userloginTQ.filter(_.username === username).take(1).result)
  }

  def insert(userLogin: UserLogin) = {
    db.run(DBIO.seq(
      userloginTQ += userLogin
    ))
  }

}