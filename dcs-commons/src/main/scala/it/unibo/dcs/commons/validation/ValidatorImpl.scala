package it.unibo.dcs.commons.validation

private[validation] final class ValidatorImpl[T](private[this] val rules: List[Rule[T]]) extends Validator[T] {

  override def validate(t: T): Unit =
    rules.find(!_.predicate(t))
      .foreach(r => throw r exception t)

}
