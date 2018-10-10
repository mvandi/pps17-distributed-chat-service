package it.unibo.dcs.service.webapp

import io.vertx.lang.scala.ScalaLogger
import io.vertx.scala.core.{DeploymentOptions, Vertx, VertxOptions}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle
import it.unibo.dcs.commons.VertxHelper.Implicits._


object Launcher extends App {

  private val logger = ScalaLogger.getLogger(getClass.getName)

  VertxHelper.toObservable[Vertx](Vertx.clusteredVertx(VertxOptions(), _))
    .subscribe(vertx => vertx deployVerticle(new WebAppVerticle, getDeploymentOptions),
      cause => logger.error("", cause))

  private def getDeploymentOptions = {
    DeploymentOptions() setConfig readServerConfiguration
  }

  private def readServerConfiguration = {
    VertxHelper.readJsonObject("/config.json")
  }
}

