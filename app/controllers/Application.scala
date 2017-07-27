package controllers

import javax.inject.Inject

import models.Animal
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.modules.reactivemongo._
import reactivemongo.play.json._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import collection._


class Application @Inject()(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)
                              extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("animal"))



  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listAnimals = Action { implicit request =>
    Ok(views.html.listAnimals(Animal.animalFormat, Animal.createAnimalForm))
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

  def editAnimal (index: Int) = Action { implicit request =>
    val formValidationResult = Animal.createAnimalForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
    }, { animal =>
      println(animal.animalType)
      val indexOfAnimal = Animal.animals.indexWhere(e => e.animalType.equalsIgnoreCase(animal.animalType))
      Animal.animals(indexOfAnimal).animalId = animal.animalId
      Animal.animals(indexOfAnimal).animalType = animal.animalType
      Animal.animals(indexOfAnimal).price = animal.price
      Animal.animals(indexOfAnimal).description = animal.description
      Animal.animals(indexOfAnimal).age = animal.age
      Animal.animals(indexOfAnimal).seller = animal.seller
      Redirect(routes.Application.listAnimals())
    })
  }

  def deleteAnimal = Action { implicit request =>
    val formValidationResult = Animal.createAnimalForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
    }, { animal =>
      println(animal.animalType)
      val indexOfAnimal = Animal.animals.indexWhere(e => e.animalType.equalsIgnoreCase(animal.animalType))
      Animal.animals(indexOfAnimal).animalId = animal.animalId
      Animal.animals(indexOfAnimal).animalType = animal.animalType
      Animal.animals(indexOfAnimal).price = animal.price
      Animal.animals(indexOfAnimal).description = animal.description
      Animal.animals(indexOfAnimal).age = animal.age
      Animal.animals(indexOfAnimal).seller = animal.seller
      Redirect(routes.Application.listAnimals())
    })
  }
}


