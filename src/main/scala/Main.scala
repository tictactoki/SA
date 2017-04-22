import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
/**
  * Created by wong on 22/04/17.
  */
object Main extends App {

  val system = ActorSystem("simple")

  implicit val timeout = Timeout(5.second)

  val referee = system.actorOf(Props[TennisReferee], name = "referee")
  referee ! TennisGame("p1","p2")
  /*val futureWinner = referee ? Won
  futureWinner.map { a =>
    println(a)}*/
  //system.terminate()




}
