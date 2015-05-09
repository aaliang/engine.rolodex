package engine.rolodex

import authentikat.jwt._

object TokenAuthenticator {
  val header = JwtHeader("HS256")

  def getToken(claimSet: Map[String, String])(implicit secretKey: String): String = {
    JsonWebToken(header, JwtClaimsSet(claimSet), secretKey)
  }

  def validateToken(jwt: String)(implicit secretKey: String): Boolean = JsonWebToken.validate(jwt, secretKey)

  def parseToken(jwt: String) = {
    jwt match {
      case JsonWebToken(header, claims, sig) =>
        claims.asSimpleMap.toOption
      case _ => None
    }
  }
}
