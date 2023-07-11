package inference

import com.stripe.rainier.compute.Real
import com.stripe.rainier.core.{Beta, Model}

/**
 * The BayesianABTest class encapsulates the Bayesian A/B test.
 *
 * We suppose our data D consists of n
 * iid observations of a Bernoulli distribution of parameter θ (conversion rate), and we want to perform inference
 * over this parameter. Suppose D contains r conversions, and let our initial belief of θ be modelled by a
 * Beta(α, β) distribution. Then, after observing our data, the posterior distribution of θ follows a
 * Beta(r+α, n-r+β).
 */
class BayesianABTest(val priorAlpha: Double, val priorBeta : Double) {

  var alpha : Double = priorAlpha
  var beta : Double = priorBeta
  var theta: Real = Beta(alpha, beta).latent

  def updateBeliefs(D: List[Long]) : Unit = {
    val n : Int = D.size
    val r : Int = D.count(_ == 1)
    // {n,r} is a sufficient statistic
    alpha += r
    beta += n - r
    theta = Beta(alpha, beta).latent
  }

  def samplePosterior(sampleSize: Int): List[Double] = {
    Model.sample(theta).take(sampleSize)
  }

  def getReport : ABTestReport = new ABTestReport(this)
}
