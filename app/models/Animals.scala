package models

import play.api.data._
import play.api.data.Forms._

import scala.collection.mutable.ArrayBuffer

case class Animal(animalType: String, price: Int)

object Animal {

  val createAnimalForm = Form(
    mapping(
      "animalType" -> nonEmptyText,
      "price" -> number(min = 0, max = 100)
    )(Animal.apply)(Animal.unapply)
  )

  val animals = ArrayBuffer(
    Animal("Dog", 99),
    Animal("Cat", 60),
    Animal("Hamster", 20)
  )

}




sd,dchjDKSFH