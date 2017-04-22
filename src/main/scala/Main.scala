import akka.actor.{ActorSystem, Props}

/**
  * Created by wong on 22/04/17.
  */
object Main extends App {

  val system = ActorSystem("simple")
  val p1 = system.actorOf(Props[TennisPlayer], name = "p1")
  val p2 = system.actorOf(Props[TennisPlayer], name = "p2")

  p1 ! Service(p2)




}
