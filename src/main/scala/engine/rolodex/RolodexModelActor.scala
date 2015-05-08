package engine.rolodex

import akka.actor.{ Props, Actor }
import java.security.{ MessageDigest, SecureRandom }
import scala.concurrent._
import ExecutionContext.Implicits.global

object RolodexModelActor {
  def props: Props = Props[RolodexModelActor]

  def name = "rolodex-model"

  case object UserNotFound
  case class UserSummary(user: UserSummary)
}

class RolodexModelActor extends Actor with RolodexLogin {

  def receive = {
    case id: String =>
      sender ! id //for now echo
    case loginParameters: LoginParameters =>

      val security = getHashAndSaltFromUsernameSomehow()

      if (security.hash == generateHash(loginParameters.password, security.salt)) {
        sender ! "ok"
      } else {
        sender ! "wrongpassword"
      }

    case createUser: CreateUser =>
      val _sender = sender
      val future = Future {
        val salt = generateSalt
        val passwordHash = generateHash(createUser.password, salt)

        UserLoginDAO.insert(passwordHash, createUser.username, createUser.email, salt)

        _sender ! "ok"
      }
  }
}

trait RolodexLogin {

  val random = new scala.util.Random(new SecureRandom())

  case class SecurityCreds(salt: String, hash: String)

  def generateSalt(): String = {
    random.nextString(12)
  }

  def generateHash(password: String, sel: String): String = {

    val sha256 = MessageDigest.getInstance("SHA-256")

    (1 to 1000)
      .toStream
      .foldLeft(sha256.digest((password + sel).getBytes))((a, _) => {
        sha256.digest((a + sel).getBytes)
      }).toString
  }

  def getHashAndSaltFromUsernameSomehow(): SecurityCreds = {
    SecurityCreds("testsalt", "testhash")
  }
}