import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props, SupervisorStrategy}
import akka.event.Logging

import scala.concurrent.Await
import akka.pattern.ask

import scala.collection.mutable
import scala.concurrent.duration._
/**
  * Created by stephane on 22/04/2017.
  */

final case class Service(actor: ActorRef)

final case class Ball(lift: Boolean)

final case object Won

final case object BallMissed

final case class TennisGame(p1: String, p2: String)

class TennisPlayer extends Actor {

  val log = Logging(context.system, this)

  override def receive = {
    case Service(opponent) =>
      log.info(s"service from ${self.path} => ${opponent.path}")
      opponent ! Ball(false)
    case Ball(lift) =>
      log.info(s"${self.path}")
      if (!lift) {
        sender ! Ball(Math.random() > 0.5)
      }
      else {
        if (Math.random() > 0.8) {
          log.info(s"${self.path} missed the ball")
          context.parent ! BallMissed
          //context.stop(self)
        }
        else {
          sender ! Ball(false)
        }
      }
  }
}

class Supervisor extends Actor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._
  import scala.concurrent.duration._


  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 1 minute) {
    case _: IndexOutOfBoundsException => Restart
  }

  override def receive = {
    case _: IndexOutOfBoundsException =>
  }
}

class TennisReferee extends Actor {

  val log = Logging(context.system, this)

  val list = scala.collection.mutable.ListBuffer[String]()

  override def receive = {

    case TennisGame(p1, p2) =>
      val player1 = context.actorOf(Props[TennisPlayer], name = p1)
      val player2 = context.actorOf(Props[TennisPlayer], name = p2)
      player1 ! Service(player2)
    case BallMissed =>
      log.info(s"${sender.path} lost")
      list.+=(s"${sender.path} won")
      self ! Won
      //context.stop(sender)
    case Won => log.info(Option(list.remove(0)).getOrElse("Nobody yet"))
  }
}