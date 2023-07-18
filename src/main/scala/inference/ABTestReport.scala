package inference

import java.util.Properties

case class ABTestReport(test: ABTest, sampleSize : Int) {
  private val posteriorSample: List[Double] = test.samplePosterior(sampleSize)
  val mean : Double = posteriorSample.sum/sampleSize
  val median : Double = (posteriorSample(sampleSize/2) + posteriorSample(sampleSize/2 + 1))/2.0

  // TODO: mode (MAP), other metric involving integration

  override def toString: String = s"Mean: ${mean}, Median: ${median}"
}
