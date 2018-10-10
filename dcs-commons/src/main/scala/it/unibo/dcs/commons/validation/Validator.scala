package it.unibo.dcs.commons.validation

trait Validator[T] {

  def validate(t: T)

}

object Validator {

  def apply[T](f: ValidatorBuilder[T] => Unit): Validator[T] = {
    val builder = new ValidatorBuilder[T]()
    f(builder)
    builder.build
  }

}
