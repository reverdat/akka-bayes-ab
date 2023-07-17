package inference

import com.stripe.rainier.compute.{Real, Vec}
import com.stripe.rainier.core.{Bernoulli, Beta, Generator, Model, Trace}
import com.stripe.rainier.sampler.EHMC

class ABTest(priorAlpha: Double = 0.5, priorBeta : Double = 0.5) {
  private val pVec : Vec[Real] = Beta(priorAlpha, priorBeta).latentVec(2)
  private var trace : Option[Trace] = None

  def observeData(data: List[(Int, Long)]): Unit = {
    val models = data.groupBy(_._1).toList.map {
      case (i, data) =>
        val conversions = data.map(_._2)
        Model.observe(conversions, Bernoulli(pVec(i)))
    }
    val mergedModel = models.reduce((m1, m2) => m1.merge(m2))
    val sampler = EHMC(5000, 5000)
    trace = Some(mergedModel.sample(sampler))
}

  def samplePosterior(sampleSize: Int): List[Double] = {
    trace match {
      case None => throw new Exception("No observed data yet")
      case Some(t: Trace) =>
        val posteriorVec: (Real, Real) = (pVec(0), pVec(1))
        val posteriorGenerators = Generator(posteriorVec)
        val thetaGenerator: Generator[Double] = posteriorGenerators map {
          case (pA, pB) =>
            pA - pB
        }
        t.predict(thetaGenerator).take(sampleSize)
    }
  }
  }



