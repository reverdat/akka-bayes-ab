package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import data.GetData
import inference.ABTestReport

import java.util.Properties


class SupervisorActor extends Actor with ActorLogging{
  import SupervisorActor._
  import ABTestActor._

  override def receive: Receive = withTestsOffline

  def withTestsOffline : Receive = {
    case InitActors(props) =>
      val n : Int = props.getProperty("constants.numTestActors").toInt
      val actorRefs : Map[Int, ActorRef] =
        (1 to n).map(i => {
            val ref : ActorRef = context.actorOf(Props[ABTestActor], s"abtest_${i}")
            context.watch(ref)
            ref ! GetProperties(props)
            i -> ref
        }).toMap
      context.become(withTestsOnline(actorRefs, props, Map[Int, ABTestReport](), 0))

    case _ => log.warning(s"No A/B test actor initialized")
  }

  def withTestsOnline(refs: Map[Int, ActorRef], properties: Properties, reports: Map[Int, ABTestReport], numTerminated: Int) : Receive = {
    case ObserveData =>
      for(kv <- refs){
        val sampleSize : Int = properties.getProperty("constants.sampleDataSize").toInt
        val data : List[(Int, Long)] = GetData.generateSampleData(sampleSize)
        kv._2 ! ObserveData(data)
      }
    case GetReports => refs.foreach(kv => kv._2 ! GetReport(kv._1))
    case GetReportResponse(id, report) =>
      log.info(s"Received A/B report from test $id: ${report.toString}")
      val updatedReports : Map[Int, ABTestReport] = reports + (id -> report)
      refs get id foreach(ref => context.stop(ref))
      context.become(withTestsOnline(refs, properties, updatedReports, numTerminated))
    case Terminated(_) =>
      log.info(s"${numTerminated + 1}/${refs.size} A/B test actors terminated")
      if(numTerminated == refs.size - 1) context.stop(self)
      else context.become(withTestsOnline(refs, properties, reports, numTerminated + 1))
  }

  override def preStart(): Unit = log.info(s"Supervisor actor started")

  override def postStop(): Unit = log.info(s"Supervisor actor stopped")

}

object SupervisorActor {
  case class InitActors(properties: Properties)
  case object ObserveData
  case class ObserveData(data: List[(Int, Long)])
  case object GetReports
  case class GetProperties(properties: Properties)
  case class GetReport(id: Int)
}
