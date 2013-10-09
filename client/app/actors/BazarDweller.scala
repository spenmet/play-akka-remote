package actors

import play.api._
import akka.actor._
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._
import akka.routing._
import com.bazar._
import com.typesafe.config._
import play.api.libs.concurrent._
import play.api.Play.current


object BazarDweller {
  
  implicit val timeout = Timeout(1 second)
  
  lazy val default = Akka.system.actorOf(Props[BazarDweller], "bazarDweller")
  
}

class BazarDweller extends Actor {
  
  val remotePath = ConfigFactory.load().getString("bazar.remotepath")
  val remoteActor = Akka.system.actorSelection(remotePath)
  
  
  def receive = {
    
    case item:Buy => {
        buyfromBazar(item)  //send the request to bazar     
    }

    case item:Bag => {
        println(s"got from bazar ${item}")
    }

    case msg => {
        println(s"got unexpected message ${msg}")
    }

    
  }

  def buyfromBazar(item:Buy) = {
    implicit val timeout = Timeout(10 seconds)
    val future = remoteActor ? item // enabled by the “ask” import
        
    try {
        val result = Await.result(future, timeout.duration)
        sender ! result
    } catch {
        case e: TimeoutException => {
            println(s"errored out ${e.getMessage}")
        }
        
    }

  }
  
  
  
}
