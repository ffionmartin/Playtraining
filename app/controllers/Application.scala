package controllers

import javax.inject.Inject

import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, _}
import play.modules.reactivemongo._
import reactivemongo.play.json._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import collection._
import play.api.libs.json._
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import models.JsonFormats._

class Application @Inject()(val messagesApi: MessagesApi, val reactiveMongoApi: ReactiveMongoApi)
                              extends Controller with I18nSupport with MongoController with ReactiveMongoComponents {

  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("animals"))

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def createAnimal: Action[AnyContent] = Action.async {

    val animal = Animal("123456", "dog", 99, "Labrador", 6, "Bob")
    val futureResult = collection.flatMap(_.insert(animal))
    futureResult.map(_ => Ok("Added animal " + animal.animalId + " " + animal.animalType))
  }

  def listAnimals: Action[AnyContent] = Action.async {
    val cursor: Future[Cursor[Animal]] = collection.map {
      _.find(Json.obj())
        .sort(Json.obj("created" -> -1))
        .cursor[Animal]
    }
    val futureAnimalsList: Future[List[Animal]] = cursor.flatMap(_.collect[List]())
    futureAnimalsList.map { animals =>
//      Ok(animals.mkString(" "))
      Ok(views.html.listAnimals(animals, JsonFormats.createAnimalForm))
    }
  }

  def updateAnimal : Action[AnyContent] = Action.async {
    val animal = Animal("777333", "cat", 99, "English Short Hair", 3, "Lucy")
    val selector = BSONDocument("animalId" -> "123456")
    val futureResult = collection.map(_.findAndUpdate(selector, animal))
    futureResult.map(_ => Ok("Updated animal"))
  }

  def removeAnimal: Action[AnyContent] = Action.async {
    val futureResult = collection.map {
      _.findAndRemove(Json.obj("animalId" -> "123456"))
    }
    futureResult.map(_ => Ok("Deleted animal"))
  }

}



//    def editAnimal(index: Int): Action[AnyContent] = Action.async {
//      val editAnimal = Animal.createAnimalForm.bindFromRequest
//      formValidationResult.fold({ formWithErrors =>
//        BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
//      }, { animal =>
//        println(animal.animalType)
//        val indexOfAnimal = Animal.animals.indexWhere(e => e.animalType.equalsIgnoreCase(animal.animalType))
//        Animal.animals(indexOfAnimal).animalId = animal.animalId
//        Animal.animals(indexOfAnimal).animalType = animal.animalType
//        Animal.animals(indexOfAnimal).price = animal.price
//        Animal.animals(indexOfAnimal).description = animal.description
//        Animal.animals(indexOfAnimal).age = animal.age
//        Animal.animals(indexOfAnimal).seller = animal.seller
//        Redirect(routes.Application.listAnimals())
//      })
//    }
//


//
//  def listAnimals = Action{ implicit request =>
//    // find all animals from the DB
//    // pass those animals for the template that's expecting them
//
//    Ok(views.html.listAnimals(Animal.animal, Animal.createAnimalForm))
//  }

//  def createAnimal = Action { implicit request =>
//
//    val formValidationResult = Animal.createAnimalForm.bindFromRequest
//    formValidationResult.fold({ formWithErrors =>
//      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
//    }, { animal =>
//      Animal.animalFormat.append(animal)
//      Redirect(routes.Application.listAnimals)
//    })
//  }
//
//  def editAnimal (index: Int) = Action { implicit request =>
//    val formValidationResult = Animal.createAnimalForm.bindFromRequest
//    formValidationResult.fold({ formWithErrors =>
//      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
//    }, { animal =>
//      println(animal.animalType)
//      val indexOfAnimal = Animal.animals.indexWhere(e => e.animalType.equalsIgnoreCase(animal.animalType))
//      Animal.animals(indexOfAnimal).animalId = animal.animalId
//      Animal.animals(indexOfAnimal).animalType = animal.animalType
//      Animal.animals(indexOfAnimal).price = animal.price
//      Animal.animals(indexOfAnimal).description = animal.description
//      Animal.animals(indexOfAnimal).age = animal.age
//      Animal.animals(indexOfAnimal).seller = animal.seller
//      Redirect(routes.Application.listAnimals())
//    })
//  }
//
//  def deleteAnimal = Action { implicit request =>
//    val formValidationResult = Animal.createAnimalForm.bindFromRequest
//    formValidationResult.fold({ formWithErrors =>
//      BadRequest(views.html.listAnimals(Animal.animals, formWithErrors))
//    }, { animal =>
//      println(animal.animalType)
//      val indexOfAnimal = Animal.animals.indexWhere(e => e.animalType.equalsIgnoreCase(animal.animalType))
//      Animal.animals(indexOfAnimal).animalId = animal.animalId
//      Animal.animals(indexOfAnimal).animalType = animal.animalType
//      Animal.animals(indexOfAnimal).price = animal.price
//      Animal.animals(indexOfAnimal).description = animal.description
//      Animal.animals(indexOfAnimal).age = animal.age
//      Animal.animals(indexOfAnimal).seller = animal.seller
//      Redirect(routes.Application.listAnimals())
//    })
//  }



