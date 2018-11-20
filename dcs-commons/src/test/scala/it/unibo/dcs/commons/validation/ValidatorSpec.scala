package it.unibo.dcs.commons.validation

import it.unibo.dcs.commons.test.JUnitSpec

final class ValidatorSpec extends JUnitSpec {

  val validator: Validator[String] = Validator[String] { _
    .addRule(_ != null, new NullPointerException("string is null"))
    .addRule(_.length > 0, new IllegalArgumentException("String is empty"))
    .addRule(_.length < 15, new IllegalArgumentException("Length is greater than 15"))
  }

  it should "throw a NullPointerException" in {
    //noinspection ScalaStyle
    validator.validate(null).subscribe(_ => (), ex => assert(ex.isInstanceOf[NullPointerException]))
  }

  it should "throw a IllegalArgumentException with message \"String is empty\"" in {
    validator.validate("").subscribe(_ => (),
      ex => assert(ex.isInstanceOf[IllegalArgumentException] && ex.getMessage == "String is empty"))
  }

  it should "throw a IllegalArgumentException with message \"Length is greater than 15\"" in {
    validator.validate("reallylongusername").subscribe(_ => (),
      ex => assert(ex.isInstanceOf[IllegalArgumentException] && ex.getMessage == "Length is greater than 15"))
  }

  it should "not throw an exception" in {
    validator.validate("mvandi").subscribe()
  }

  it should "not IllegalStateException since builder has already been built" in {
    var builder: ValidatorBuilder[String] = null

    Validator[String] { b =>
      builder = b
    }
    val thrown = intercept[IllegalStateException] {
      builder.build
    }
    assert(thrown.getMessage == "ValidatorBuilder has already been built")
  }

}
