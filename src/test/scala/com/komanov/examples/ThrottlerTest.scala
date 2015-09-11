package com.komanov.examples

import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger}
import java.util.concurrent.{CountDownLatch, Executors, Semaphore, TimeUnit}

import org.specs2.mutable.{After, Specification}

import scala.concurrent.{ExecutionContext, Future}

class ThrottlerTest extends Specification {

  "Throttler" should {
    "execute sequential" in new ctx {
      var invocationCount = 0
      for (i <- 0 to maxCount) {
        throttler {
          invocationCount += 1
        }
      }
      invocationCount must be_==(maxCount + 1)
    }

    "throw exception once reached the limit [naive, flaky]" in new ctx {
      for (i <- 0 until maxCount) {
        Future {
          throttler(waitForever())
        }
      }

      throttler(waitForever()) must throwA[ThrottledException]
    }

    "throw exception once reached the limit [naive, bad]" in new ctx {
      for (i <- 0 until maxCount) {
        Future {
          throttler(waitForever())
        }
      }

      Thread.sleep(1000)

      throttler(waitForever()) must throwA[ThrottledException]
    }

    "throw exception once reached the limit [still flaky]" in new ctx {
      val barrier = new CountDownLatch(1)
      for (i <- 0 until maxCount) {
        Future {
          throttler {
            barrier.countDown()
            waitForever()
          }
        }
      }

      barrier.await()
      throttler(waitForever()) must throwA[ThrottledException]
    }

    "throw exception once reached the limit [working]" in new ctx {
      var success = new AtomicBoolean()
      val exceptionLatch = new CountDownLatch(1)
      val activeCount = new AtomicInteger()

      for (i <- 0 to maxCount) {
        Future {
          activeCount.incrementAndGet()
          try {
            throttler(waitForever())
          } finally {
            activeCount.decrementAndGet()
          }
        }.onFailure({
          case e: ThrottledException =>
            success.set(true)
            exceptionLatch.countDown()
          case ex =>
            ex.printStackTrace()
        })
      }

      exceptionLatch.await(5, TimeUnit.SECONDS) must beTrue
      success.get() must beTrue
      activeCount.get() must be_==(maxCount)
    }
  }

  trait ctx extends After {
    val maxCount = 3
    val throttler = new Throttler(maxCount)

    val e = Executors.newCachedThreadPool()
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(e)

    private val waitForeverLatch = new CountDownLatch(1)

    override def after: Any = {
      waitForeverLatch.countDown()
      e.shutdownNow()
    }

    def waitForever(): Unit = {
      waitForeverLatch.await()
    }
  }
}

class ThrottledException extends RuntimeException("Throttled!")

class Throttler(count: Int) {
  require(count > 0)

  private val semaphore = new Semaphore(count)

  def apply(f: => Unit): Unit = {
    if (!semaphore.tryAcquire()) {
      throw new ThrottledException
    }

    try {
      f
    } finally {
      semaphore.release()
    }
  }
}
