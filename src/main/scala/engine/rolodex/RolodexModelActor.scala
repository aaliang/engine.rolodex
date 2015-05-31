package engine.rolodex

import akka.actor.{ Props, Actor }
import java.security.{ MessageDigest, SecureRandom }
import scala.concurrent._
import scala.util.{ Success, Failure }
import ExecutionContext.Implicits.global

object RolodexModelActor {

  def props: Props = Props[RolodexModelActor]

  def name = "rolodex-model"

}

class RolodexModelActor extends Actor with RolodexLogin {

  def receive = {
    case loginParameters: LoginParameters =>
      val _sender = sender
      UserLoginVAO.selectByUsername(loginParameters.username) onComplete {
        case Success(user) =>
          generateHash(loginParameters.password, user.salt) match {
            case user.hash => _sender ! user
            case _ => _sender ! "bad"
          }
        case Failure(e) =>
          _sender ! "bad"
      }
    case createUser: CreateUser =>

      val _sender = sender
      val salt = generateSalt
      val passwordHash = generateHash(createUser.password, salt)

      val login = UserLogin(
        passwordHash,
        createUser.username.toLowerCase,
        salt,
        createUser.email,
        createUser.role)

      UserLoginVAO.insert(login).onComplete {
        case Success(e) =>
          _sender ! login
        case Failure(e) =>
          _sender ! null
      }
  }
}

trait RolodexLogin {

  val random = new scala.util.Random(new SecureRandom())

  case class SecurityCreds(salt: String, hash: String)

  /**
   * Generates a secure random salt
   */
  def generateSalt(): String = {
    random.nextString(16)
  }

  /**
   * Given a password and salt, returns the validation hash
   *
   * @param password the string to use as the password, in plaintext
   * @param sel a random salt, ideally generated from [[generateSalt]]
   */
  def generateHash(password: String, sel: String): String = {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val init = new String(sha256.digest((password + sel).getBytes))
    (1 to 1000)
      .toStream
      .foldLeft(init)((a, _) => new String(sha256.digest((a + sel).getBytes)))
  }
}
