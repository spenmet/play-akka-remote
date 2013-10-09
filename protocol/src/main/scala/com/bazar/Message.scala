/**
* @author satish
* sample protocol between client and server
*/
package com.bazar


sealed trait Message
case class Buy(id:String, item:Item) extends Message
case class Ack(status:Status) extends Message
case class Bag(requestId:String, item:Item, price:Int) extends Message


sealed trait Item extends Serializable { def name:String }
class KoraMenu(override val name:String = "very tasty ... ;") extends Item 
class Kaddu(override val name:String =  "just a veggie") extends Item 
class Kheema(override val name:String = "yumm ! you know what I am saying") extends Item 
class Rice(override val name:String = "you can have lots of it") extends Item 

sealed trait Status  { def name:String }
case class KalAjao(override val name:String = "come tommorrow") extends Status 
case class InAMinute(override val name:String = "needs some work") extends Status 
case class VerySoon(override val name:String = "not a problem") extends Status 
case class HereYouGo(override val name:String = "help yourslef") extends Status 



