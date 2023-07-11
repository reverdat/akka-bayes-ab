package actors

import akka.actor.{Actor, ActorLogging}
import inference.{ABTestReport, BayesianABTest}

class ABTestActor extends Actor with ActorLogging {
  import SupervisorActor._
  import ABTestActor._

  //TODO: Get params from config
  override def receive: Receive = currentBelief(new BayesianABTest(0.5, 0.5))

  def currentBelief(test: BayesianABTest) : Receive = {
    case ObserveData(data) =>
      test.updateBeliefs(data)
      context.become(currentBelief(test))
    case SamplePosterior(sampleSize) =>
      val sample: List[Double] = test.samplePosterior(sampleSize)
      context.sender() ! SamplePosteriorResponse(sample)
    case GetReport(id) => context.sender() ! GetReportResponse(id, test.getReport) //TODO: Report current A/B Test results
  }

  override def preStart(): Unit = log.info(s"A/B Test actor started")
  override def postStop(): Unit = log.info(s"A/B Test actor stopped")
}

object ABTestActor {
  case class GetReportResponse(id: Int, report: ABTestReport)
  case class SamplePosteriorResponse(sample: List[Double])
}
