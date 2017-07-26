package controllers

import javax.inject.Inject

import models.Animal
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.Future

class Application @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listAnimals = Action { implicit request =>
    Ok(views.html.listAnimals(Animal.animals, Animal.createAnimalForm))
  }

  def viewAnimals = Action { implicit request =>
    Ok(views.html.viewAnimals(Animal.animals))
  }

  def createAnimal = Action { implicit request =>

    val formValidationResult = Animal.createAnimalForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
    }, { animal =>
      Animal.animals.append(animal)
      Redirect(routes.Application.listAnimals)
    })
  }

}
