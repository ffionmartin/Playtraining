import play.api.data.Form
import play.api.data.Forms._

package models {

  import play.api.libs.json.OFormat

  import scala.collection.mutable.ArrayBuffer

  case class Animal(
                     index: Option[Int],
                     animalId: String,
                     animalType: String,
                     price: Int,
                     description: String,
                     age: Int,
                     seller: String
                   )


  object JsonFormats {

    import play.api.libs.json.Json

    implicit val animalDataFormat: OFormat[Animal] = Json.format[Animal]

    val createAnimalForm: Form[Animal] = Form(
      mapping(
        "index" -> optional(number),
        "animalId" -> nonEmptyText,
        "animalType" -> nonEmptyText,
        "price" -> number(min = 0, max = 100),
        "description" -> nonEmptyText,
        "age" -> number(min = 0, max = 100),
        "seller" -> nonEmptyText
      )(Animal.apply)(Animal.unapply)
    )

    val animals: ArrayBuffer[Animal] = ArrayBuffer[Animal]()

  }

}
