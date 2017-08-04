package controllers

import javax.inject.Inject

import models.JsonFormats._
import models._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc.{AnyContent, _}
import play.modules.reactivemongo._
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json._
import reactivemongo.play.json.collection._
import scala.concurrent.Future

class Application @Inject()(val messagesApi: MessagesApi, environment: play.api.Environment, val reactiveMongoApi: ReactiveMongoApi) extends Controller
  with I18nSupport with MongoController with ReactiveMongoComponents {


  def collection: Future[JSONCollection] = database.map(_.collection[JSONCollection]("animals"))

  def hello(name: String): Action[AnyContent] = Action { implicit request =>
    Ok("Hello " + name).withSession("name" -> name)
  }



  def index = Action { implicit request =>
    Ok(views.html.index("Your new application is ready."))
  }

  def findByIndex(index: Int): Action[AnyContent] = Action.async { implicit request =>

    val futures = for {
      all <- getResults(Json.obj())
      one <- getResults(Json.obj("index" -> index))
    } yield (all, one)

    futures.map {
      case (a, b) => {
        //println("one size is " + a.size)
        Ok(views.html.listAnimals(a, JsonFormats.createAnimalForm.fill(b.head)))
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
    val postAction = request.body.asFormUrlEncoded("action")(0)
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
      futureResult.map(_ =>
        //        println("about to redirect...")
        Redirect(routes.Application.listAnimals))

    })
  }
}



