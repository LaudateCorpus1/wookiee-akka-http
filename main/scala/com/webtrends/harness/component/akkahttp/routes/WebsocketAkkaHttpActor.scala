package com.webtrends.harness.component.akkahttp.routes

import akka.actor.Props
import akka.http.scaladsl.server.Directives.reject
import akka.http.scaladsl.settings.ServerSettings
import akka.http.scaladsl.server.Directives._
import com.webtrends.harness.component.akkahttp.WebsocketAkkaHttpSettings
import com.webtrends.harness.health.{ComponentState, HealthComponent}

import scala.concurrent.Future

object WebsocketAkkaHttpActor {
  def props(settings: WebsocketAkkaHttpSettings) = {
    Props(classOf[WebsocketAkkaHttpActor], settings.port, settings.interface, settings.serverSettings)
  }
}

class WebsocketAkkaHttpActor(port: Int, interface: String, settings: ServerSettings)
  extends ExternalAkkaHttpActor(port, interface, settings) {
  override def serverName = "akka-http websocket-server"

  override def routes = if (WebsocketAkkaHttpRouteContainer.isEmpty) {
    log.debug("no routes defined")
    reject()
  } else {
    WebsocketAkkaHttpRouteContainer.getRoutes.reduceLeft(_ ~ _)
  }

  override def checkHealth: Future[HealthComponent] = {
    Future.successful(HealthComponent("WebsocketAkkaHttpActor", ComponentState.NORMAL, "Websocket Actor Up"))
  }
}
