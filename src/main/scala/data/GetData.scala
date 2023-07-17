package data

import breeze.stats.distributions.Rand.VariableSeed.randBasis
import breeze.stats.distributions.{Uniform, Bernoulli}

object GetData {

  def generateSampleData(sampleSize: Int) : List[(Int, Long)] = {
    val pRealControl = Uniform(0,1).sample(1).head
    val pRealTreatment = Uniform(0,1).sample(1).head
    val dataControl = Bernoulli(pRealControl).sample(sampleSize).map(b => if (b) 1L else 0L).map((0, _)).toList
    val dataTreatment = Bernoulli(pRealTreatment).sample(sampleSize).map(b => if (b) 1L else 0L).map((1, _)).toList
    dataControl ++ dataTreatment
  }

}
