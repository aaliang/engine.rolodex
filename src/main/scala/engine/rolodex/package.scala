package engine.rolodex

case class User(id: Int, stock: Int, title: String, desc: String)

case class LoginParameters(username: String, password: String)

case class CreateUser(username: String, password: String)

