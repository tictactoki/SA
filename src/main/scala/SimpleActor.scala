import akka.actor.{Actor, ActorRef}
import akka.event.Logging

/**
  * Created by stephane on 22/04/2017.
  */

final case class Service(actor: ActorRef)
final case class Ball(lift: Boolean)
final case object Won

class TennisPlayer extends Actor {

  val log = Logging(context.system,this)

  override def receive = {
    case Service(opponent) =>
      log.info(s"service from ${self.path} => ${opponent.path}")
      opponent ! Ball(false)
    case Ball(lift) =>
      log.info(s"${self.path}")
      if(!lift) {
        sender ! Ball(Math.random() > 0.5)
      }
      else {
        if(Math.random() > 0.8) {
          log.info(s"${self.path} missed the ball")
          sender ! Won
          context.stop(self)
        }
        else {
          sender ! Ball(false)
        }
      }
    case Won =>
      log.info(s"${self.path} won")
      context.stop(self)
  }
}
