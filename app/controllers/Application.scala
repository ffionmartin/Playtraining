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
import play.api.data.Form
import play.api.libs.Files

class Application @Inject()(val messagesApi: MessagesApi, environment: play.api.Environment, val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with I18nSupport with MongoController with ReactiveMongoComponents {


  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("animals"))

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def findByIndex(index: Int) = Action.async { implicit request =>

    val futures = for {
      all <- getResults(Json.obj())
      one <- getResults(Json.obj("index" -> index))
    } yield (all, one)

    futures.map {
      case (all, one) => {
        println("one size is " + one.size)
        Ok(views.html.listAnimals(all, JsonFormats.createAnimalForm.fill(one.head)))
      }

    }
  }

  private def getResults(selector: JsObject): Future[List[Animal]] = {
    val cursor: Future[Cursor[Animal]] = collection.map {
      _.find(selector)
        .sort(Json.obj("index" -> 1)).
        cursor[Animal]
    }
    cursor.flatMap(_.collect[List]())
  }

  def listAnimals: Action[AnyContent] = Action.async { implicit request =>

    getResults(Json.obj()).map { animals =>
      Ok(views.html.listAnimals(animals, JsonFormats.createAnimalForm))
    }
  }

  def createAnimal = Action.async(parse.multipartFormData) { implicit request =>
    val postAction = request.body.asFormUrlEncoded.get("action").get(0)
    val boundForm = JsonFormats.createAnimalForm.bindFromRequest
    boundForm.fold({ formWithErrors =>
      getResults(Json.obj()).map { animals =>
        BadRequest(views.html.listAnimals(animals, formWithErrors))
      }
    }, { animal =>
      println("postAction is " + postAction)
      val selector = BSONDocument("index" -> animal.index.getOrElse(0))
      val futureResult = postAction match {
        case "save" if animal.index.get != -1 => collection.map(_.update(selector, animal, upsert = true))
        case "delete" if animal.index.get != -1 => collection.flatMap(_.remove(selector))
        case _ => collection.flatMap(_.insert(animal))
      }
      futureResult.map(_ => {
//        println("about to redirect...")
        Redirect(routes.Application.listAnimals)
      })


//      Future(Redirect(routes.Application.listAnimals))
    })
  }
}


