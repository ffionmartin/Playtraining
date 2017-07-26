package controllers

import play.api.mvc._

class Email extends Controller {

  def inbox = Action {
      Redirect("http://hotmail.com")
  }

}