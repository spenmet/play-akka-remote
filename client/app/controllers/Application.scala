package controllers

import play.api._
import play.api.mvc._
import akka.actor._
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import akka.routing._
import com.bazar._
import actors.BazarDweller
import com.typesafe.config._
import play.api.libs.concurrent._
import play.api.Play.current

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("hello"))
  }

  def buyItems = Action {
    implicit val timeout = Timeout(10 seconds)
    val future = BazarDweller.default ? Buy("123", new Kheema) 
    
    try {
      val result = Await.result(future, timeout.duration)
      Ok(s"got result: ${result}")
    } catch {
      case e: TimeoutException => {
        Ok(s"errored out ${e.getMessage}")
      }
    }
  }

}