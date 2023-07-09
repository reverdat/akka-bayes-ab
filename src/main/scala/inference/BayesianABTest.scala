package inference

import com.stripe.rainier.compute.Real
import com.stripe.rainier.core.{Beta, Model}

class BayesianABTest(val priorAlpha: Double, val priorBeta : Double) {
  var alpha : Double = priorAlpha
  var beta : Double = priorBeta
  var lambda: Real = Beta(alpha, beta).latent // Prior Beta distribution

  def updateBeliefs(data: List[Int]) : Unit = {
    /*
    * Prior: Beta(alpha, beta)
    * Likelihood: Binomial(N, numConversions)
    * => Posterior: Beta(alpha + numConversions, beta + numNonConversions)
    * */

    val N : Int = data.size
    val numConversions : Int = data.count(_ == 1)
    val numNonConversions : Int = N - numConversions
    alpha += numConversions
    beta += numNonConversions
    lambda = Beta(alpha, beta).latent
  }

  def samplePosterior(sampleSize: Int): List[Double] = {
    Model.sample(lambda).take(sampleSize)
  }
}
