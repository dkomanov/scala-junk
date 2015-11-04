package com.komanov.uuid

import java.util.UUID

/*
The lastest run result:

original avg 423 ns, total 4237782281
java 0   avg 471 ns, total 4713609987
java 1   avg 354 ns, total 3544927207
java 2   avg 213 ns, total 2138386912
java 3   avg 143 ns, total 1438326044
java 4   avg 109 ns, total 1099210061
java 5   avg  77 ns, total  778478559
 */
object GuidParserPerformanceTest extends App {

  val N = 10000000

  type ParserFunc = String => UUID

  val uuid1 = "00000000-0000-0000-0000-000000000000"
  val uuid2 = "01234567-89ab-cdef-ABCD-EF1234567890"
  val uuid3 = "ffffffff-ffff-ffff-ffff-ffffffffffff"

  val originalF: ParserFunc = UUID.fromString
  //val scalaFastF: ParserFunc = UuidScalaUtils.fromStringFast
  val javaFast0F: ParserFunc = UuidJava0Utils.fromStringFast
  val javaFast1F: ParserFunc = UuidJava1Utils.fromStringFast
  val javaFast2F: ParserFunc = UuidJava2Utils.fromStringFast
  val javaFast3F: ParserFunc = UuidJava3Utils.fromStringFast
  val javaFast4F: ParserFunc = UuidJava4Utils.fromStringFast
  val javaFast5F: ParserFunc = UuidJava5Utils.fromStringFast

  doFuncTest()

  //doTest("scala   ", scalaFastF)
  doTest("original", originalF)
  doTest("java 0  ", javaFast0F)
  doTest("java 1  ", javaFast1F)
  doTest("java 2  ", javaFast2F)
  doTest("java 3  ", javaFast3F)
  doTest("java 4  ", javaFast4F)
  doTest("java 5  ", javaFast5F)

  def doTest(name: String, f: ParserFunc): Unit = {
    // warm up
    for (i <- 0 until (N / 10)) {
      f(uuid2)
    }

    val start = System.nanoTime()
    for (_ <- 0 until N) {
      f(uuid2)
    }
    val duration = System.nanoTime() - start
    val avg = duration / N
    println(s"$name avg $avg ns, total $duration")
  }

  def doFuncTest() = {
    val algorithms = Seq(originalF, /*scalaFast,F*/ javaFast0F, javaFast1F, javaFast2F, javaFast3F, javaFast4F, javaFast5F)
    val uuids = Seq(uuid1, uuid2, uuid3)

    algorithms.foreach(a => new UuidTest(a).run())

    val results = algorithms.map(a => uuids.map(a))
    val result = results.head
    for (r <- results) {
      require(r == result)
    }
  }

}
