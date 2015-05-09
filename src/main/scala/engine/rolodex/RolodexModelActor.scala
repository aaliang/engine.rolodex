package engine.rolodex

import akka.actor.{ Props, Actor }
import java.security.{ MessageDigest, SecureRandom }
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.util.Arrays

object RolodexModelActor {

  def props: Props = Props[RolodexModelActor]

  def name = "rolodex-model"

  case object UserNotFound
  case class UserSummary(user: UserSummary)

}

class RolodexModelActor extends Actor with RolodexLogin {

  def receive = {
    case id: String => sender ! "ok"
    case loginParameters: LoginParameters =>
      val _sender = sender
      UserLoginDAO.selectByUsername(loginParameters.username).onSuccess {
        case Seq(ulogin: UserLogin) => {
          val hash = generateHash(loginParameters.password, ulogin.salt)

          if (hash == ulogin.hash) {
            _sender ! "ok"
          } else {
            _sender ! "invalid"
          }

        }
        case _ => _sender ! "invalid"
      }
    case createUser: CreateUser =>
      val _sender = sender

      Future {
        val salt = generateSalt
        val passwordHash = generateHash(createUser.password, salt)

        UserLoginDAO
          .insert(UserLogin(passwordHash, createUser.username, createUser.email, salt))
          .onSuccess {
            case _ => _sender ! "ok"
          }
      }
  }
}

trait RolodexLogin {

  val random = new scala.util.Random(new SecureRandom())

  case class SecurityCreds(salt: String, hash: String)

  def generateSalt(): String = {
    random.nextString(16)
  }

  def generateHash(password: String, sel: String): String = {

    val sha256 = MessageDigest.getInstance("SHA-256")
    val init = new String(sha256.digest((password + sel).getBytes))
    (1 to 1000)
      .toStream
      .foldLeft(init)((a, _) => new String(sha256.digest((a + sel).getBytes)))

  }

  def getHashAndSaltFromUsernameSomehow(): SecurityCreds = {
    SecurityCreds("testsalt", "testhash")
  }
}