package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import data.GetData
import inference.ABTestReport


class SupervisorActor extends Actor with ActorLogging{
  import SupervisorActor._
  import ABTestActor._

  override def receive: Receive = withTestsOffline

  def withTestsOffline : Receive = {
    case InitActors(n) =>
      val actorRefs : Map[Int, ActorRef] =
        (1 to n).map(i => {
            val ref : ActorRef = context.actorOf(Props[ABTestActor], s"abtest_${i}")
            context.watch(ref)
            i -> ref
        }).toMap
      context.become(withTestsOnline(actorRefs, Map[Int, ABTestReport](), 0))

    case _ => log.warning(s"No A/B test actor initialized")
  }

  def withTestsOnline(refs: Map[Int, ActorRef], reports: Map[Int, ABTestReport], numTerminated: Int) : Receive = {
    case ObserveData =>
      for(kv <- refs){
        val data : List[(Int, Long)] = GetData.generateSampleData(1000)
        kv._2 ! ObserveData(data)
      }
    case GetReports => refs.foreach(kv => kv._2 ! GetReport(kv._1))
    case GetReportResponse(id, report) =>
      log.info(s"Received A/B report from test $id: ${report.toString}")
      val updatedReports : Map[Int, ABTestReport] = reports + (id -> report)
      refs get id foreach(ref => context.stop(ref))
      context.become(withTestsOnline(refs, updatedReports, numTerminated))
    case Terminated(_) =>
      log.info(s"${numTerminated + 1}/${refs.size} A/B test actors terminated")
      if(numTerminated == refs.size - 1) {
        reports.foreach(kv => log.info(s"A/B Test ${kv._1}: ${kv._2.toString}"))
        context.stop(self)
      }
      else context.become(withTestsOnline(refs, reports, numTerminated + 1))
  }

  override def preStart(): Unit = log.info(s"Supervisor actor started")

  override def postStop(): Unit = log.info(s"Supervisor actor stopped")

}

object SupervisorActor {
  case class InitActors(numActors: Int)
  case object ObserveData
  case class ObserveData(data: List[(Int, Long)])
  case class SamplePosterior(sampleSize: Int)
  case object GetReports
  case class GetReport(id: Int)
}
