package com.komanov.test

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class GetOrThrowTest extends Specification with Mockito {
  "Option.getOrElse" should {
    "throw on None" in {
      val o = Option[String](null)
      o.getOrElse(throw new MyException(true)) must throwA[MyException]
    }

    "not throw on Some" in {
      val o = Option("1")
      o.getOrElse(throw new MyException(false)) === "1"
    }
  }

  class MyException(shouldBeThrow: Boolean) extends Throwable {
    require(shouldBeThrow)
  }

}
