/**
* @author satish
* sample actor system
*/
package com.bazar

import akka.actor._
import akka.routing._
import akka.kernel.Bootable


class RaituBazar extends Bootable {

  val bazarSystem = ActorSystem("bazarSystem")
  val raituRouter = bazarSystem.actorOf(Props[RaituDukan].withRouter(SmallestMailboxRouter(5)), "raituRouter" )

  def startup = {
    println(s"system started")
    println(s"raituRouter created:${raituRouter}")
  }
 
  def shutdown = {
    bazarSystem.shutdown()
  }
}


object RaituBazarApp {
  def main(args: Array[String]) {
    new RaituBazar
    println("Started Raitu Bazar Application - waiting for messages")
  }
}


class RaituDukan extends Actor with ActorLogging {
    
    def receive = {
        case Buy(id:String, item:KoraMenu) => sender ! Ack(KalAjao())
       
        case Buy(id, item:Kheema) => {
            println(s"RaituDukan recieved ${item.name}")
            println(s"sending Ack")
            sender ! Ack(InAMinute())
            println(s"sent Ack")

            val kheema:Item = chopMutton
            println(s"sending Bag")
            sender ! Bag(id, kheema, 700)
            println(s"sent Bag") 
        }

        case Buy(id, item:Kaddu) => {
            sender ! Ack(HereYouGo())
            sender ! Bag(id, item, 10) 
        }
    }

    def chopMutton = {
        import scala.math._

        def chop(times:Int):Double = {
            if (times > 0) chop(times-1)
            Math.sqrt(times)
        }
        chop(1000)

        new Kheema
    }
}

