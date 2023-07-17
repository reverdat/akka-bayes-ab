package actors

import akka.actor.{Actor, ActorLogging}
import inference.{ABTest, ABTestReport}

class ABTestActor extends Actor with ActorLogging {
  import SupervisorActor._
  import ABTestActor._

  override def receive: Receive = currentBelief(new ABTest())

  def currentBelief(test: ABTest) : Receive = {
    case ObserveData(data) =>
      test.observeData(data)
      context.become(currentBelief(test))
    case GetReport(id) =>
      val report : ABTestReport = ABTestReport(test)
      context.sender() ! GetReportResponse(id, report)
  }

  override def preStart(): Unit = log.info(s"A/B Test actor started")
  override def postStop(): Unit = log.info(s"A/B Test actor stopped")
}

object ABTestActor {
  case class GetReportResponse(id: Int, report: ABTestReport)
  case class SamplePosteriorResponse(sample: List[Double])
}
