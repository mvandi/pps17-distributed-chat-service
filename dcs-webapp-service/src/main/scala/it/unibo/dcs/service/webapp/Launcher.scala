package it.unibo.dcs.service.webapp

import io.vertx.scala.core.{DeploymentOptions, Vertx}
import it.unibo.dcs.commons.VertxHelper
import it.unibo.dcs.service.webapp.verticles.WebAppVerticle

object Launcher extends App {

  Vertx vertx() deployVerticle(new WebAppVerticle, getDeploymentOptions)

  private def getDeploymentOptions = {
    DeploymentOptions() setConfig readServerConfiguration
  }

  private def readServerConfiguration = {
    VertxHelper.readJsonObject("/config.json")
  }
}

