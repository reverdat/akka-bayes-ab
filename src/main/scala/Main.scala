import java.io.FileReader
import java.util.Properties

import actors.SupervisorActor
import akka.actor.{ActorSystem, Props}

object Main extends App {

  import SupervisorActor._

  val properties = new Properties()
  properties.load(new FileReader("config.properties"))

  val system = ActorSystem("AkkaBayesDemo")
  val supervisorActor = system.actorOf(Props[SupervisorActor], "supervisorActor")

  supervisorActor ! InitActors(properties)
  supervisorActor ! ObserveData
  supervisorActor ! GetReports

}
