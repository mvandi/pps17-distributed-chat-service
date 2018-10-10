package it.unibo.dcs.commons

import io.vertx.rx.java.{RxHelper => JRxHelper}
import io.vertx.scala.core.{Context, Vertx}
import it.unibo.dcs.commons.RxHelper.Implicits._
import it.unibo.dcs.commons.VertxHelper.Implicits._
import rx.lang.scala.{Observable, Scheduler}

object RxHelper {

  def scheduler(context: Context): Scheduler = JRxHelper.scheduler(context)

  def scheduler(vertx: Vertx): Scheduler = JRxHelper.scheduler(vertx)

  def blockingScheduler(vertx: Vertx, ordered: Boolean = DEFAULT_ORDERED): Scheduler = JRxHelper.blockingScheduler(vertx, ordered)

  private val DEFAULT_ORDERED: Boolean = false

  private[commons] object Implicits {

    implicit final class RxScheduler(val asJavaScheduler: rx.Scheduler) extends Scheduler

    implicit final class RxObservable[T](val asJavaObservable: rx.Observable[T]) extends Observable[T]

  }

}
