package actors

import akka.actor.{Actor, ActorLogging}
import inference.{ABTest, ABTestReport}

import java.util.Properties

class ABTestActor extends Actor with ActorLogging {
  import SupervisorActor._
  import ABTestActor._

  override def receive: Receive = currentBelief(new ABTest(), new Properties())

  def currentBelief(test: ABTest, properties: Properties) : Receive = {
    case GetProperties(props) =>
      context.become(currentBelief(test, props))
    case ObserveData(data) =>
      test.observeData(data)
      context.become(currentBelief(test, properties))
    case GetReport(id) =>
      val sampleSize : Int = properties.getProperty("constants.posteriorSampleSize").toInt
      val report : ABTestReport = ABTestReport(test, sampleSize)
      context.sender() ! GetReportResponse(id, report)
  }

  override def preStart(): Unit = log.info(s"A/B Test actor started")
  override def postStop(): Unit = log.info(s"A/B Test actor stopped")
}

object ABTestActor {
  case class GetReportResponse(id: Int, report: ABTestReport)
}
