package engine.rolodex

import slick.driver.PostgresDriver.api._

import engine.BaseDAO

case class UserLogin(
  hash: String,
  username: String,
  salt: String,
  email: Option[String],
  role: Option[Int])

class UserLogins(tag: Tag) extends Table[UserLogin](tag, "user_login") {
  def hash = column[String]("hash")
  def username = column[String]("username")
  def email = column[String]("email")
  def salt = column[String]("salt")
  def role = column[Int]("role", O.Default(0))

  def * = (hash, username, salt, email.?, role.?) <> (UserLogin.tupled, UserLogin.unapply _)
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