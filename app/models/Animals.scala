package models

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class Animal(animalType: String, price: Int, description: String, age: Int, seller: String)

object Animal {

  val createAnimalForm = Form(
    mapping(
      "animalType" -> nonEmptyText,
      "price" -> number(min = 0, max = 100),
      "description" -> nonEmptyText,
      "age" -> number(min = 0, max = 100),
      "seller" -> nonEmptyText
    )(Animal.apply)(Animal.unapply)
  )

  val animals = ArrayBuffer(
    Animal("Dog", 99, "Labrador", 6, "Bob"),
    Animal("Cat", 60, "Siamese", 3, "Julie"),
    Animal("Hamster", 20,"Brown and White", 1, "Amber")
  )

}


