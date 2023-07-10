package actors

import akka.actor.Actor
import inference.{BayesianABTest, ABTestReport}

class ABTestActor extends Actor {
  import SupervisorActor._
  import ABTestActor._

  //TODO: Get params from config
  override def receive: Receive = currentBelief(new BayesianABTest(1, 1))

  def currentBelief(test: BayesianABTest) : Receive = {
    case ObserveData(data) =>
      test.updateBeliefs(data)
      context.become(currentBelief(test))
    case GetReport => ??? //TODO: Report current A/B Test results
    case SamplePosterior(sampleSize) =>
      val sample: List[Double] = test.samplePosterior(sampleSize)
      context.sender() ! SamplePosteriorResponse(sample)
  }
}

object ABTestActor {
  case class GetReportResponse(report: ABTestReport)
  case class SamplePosteriorResponse(sample: List[Double])
}
