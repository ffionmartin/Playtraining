package controllers

import actions.{LoggingAction, LoggingAuthenticatedAction}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class CompositionController extends Controller {


  def something = LoggingAction.async {

    Future(Ok("foo with action logging thru action composition"))
  }
  def authenticated: Action[AnyContent] = LoggingAuthenticatedAction.instance { request =>
       Ok("hello " + request.session.get("name").getOrElse("Anon") + " you are authentic")
  }

}