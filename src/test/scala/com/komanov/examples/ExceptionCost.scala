package com.komanov.examples

/*
the latest run:

convert  avg 2 ns, total 24027983
ctor     avg 23 ns, total 233135146
create   avg 1456 ns, total 14560671210
 */
object ExceptionCost extends App {

  private val N = 10000000

  type ConvertFunc = () => Unit

  val twitterTry = com.twitter.util.Throw(new RuntimeException)

  val convertF: ConvertFunc = convertException
  val ctorF: ConvertFunc = constructorException
  val createF: ConvertFunc = createException

  val algorithms = Map(
    "convert " -> convertF,
    "ctor    " -> ctorF,
    "create  " -> createF
  )

  doWarmUp()
  for ((name, f) <- algorithms) {
    doTest(name, f)
  }

  private def doTest(name: String, f: ConvertFunc): Unit = {
    val start = System.nanoTime()
    for (_ <- 0 until N) {
      f()
    }
    val duration = System.nanoTime() - start
    val avg = duration / N
    println(s"$name avg $avg ns, total $duration")
  }

  private def doWarmUp() = {
    // warm up
    for (i <- 0 until (N / 10)) {
      for (a <- algorithms) {
        a._2()
      }
    }
  }

  private def convertException(): Unit = {
    if (twitterTry.isThrow) scala.util.Failure(twitterTry.e) else scala.util.Success(twitterTry.get)
  }

  private def constructorException(): Unit = {
    scala.util.Try(twitterTry.get())
  }

  private def createException(): Unit = {
    new RuntimeException
  }

}
