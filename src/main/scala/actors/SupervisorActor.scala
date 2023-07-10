package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

class SupervisorActor extends Actor with ActorLogging{
  import SupervisorActor._
  import ABTestActor._

  override def receive: Receive = withTestsOffline

  def withTestsOffline : Receive = {
    case InitActors(n) =>
      val actorRefs : Map[Int, ActorRef] =
        (1 to n).map(i => i -> context.actorOf(Props[ABTestActor], s"abtest_${i}")).toMap
      context.become(withTestsOnline(actorRefs))

    case _ => log.warning("[SupervisorActor] No ABTestActor initialized")
  }

  def withTestsOnline(refs: Map[Int, ActorRef]) : Receive = {
    ???
  }

}

object SupervisorActor {
  case class InitActors(numActors: Int)
  case class ObserveData(data: List[Int])
  case class SamplePosterior(sampleSize: Int)
  case object GetReport
}
