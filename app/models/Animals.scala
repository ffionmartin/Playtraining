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
//
//
//package models
//import play.api.data._
//import play.api.data.Forms._
//import play.api.libs.json.Json
//import scala.collection.mutable.ArrayBuffer

//
//case class Animal(var animalId: String, var animalType: String, var price: Int, var description: String, var age: Int, var seller: String)
//
//
//
//object Animal {
//
//  val createAnimalForm = Form(
//    mapping(
//      "animalId" -> nonEmptyText,
//      "animalType" -> nonEmptyText,
//      "price" -> number(min = 0, max = 100),
//      "description" -> nonEmptyText,
//      "age" -> number(min = 0, max = 100),
//      "seller" -> nonEmptyText
//    )(Animal.apply)(Animal.unapply)
//  )
//
//  implicit val animalFormat = Json.format[Animal]
//
//}


//  val animals = ArrayBuffer(
//    Animal("123456","dog", 99, "Labrador", 6, "Bob"),
//    Animal("54321","Cat", 60, "Siamese", 3, "Julie"),
//    Animal("321321","Hamster", 20,"Brown and White", 1, "Amber")
//  )