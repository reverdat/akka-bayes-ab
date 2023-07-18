package utils

import breeze.linalg.DenseVector

import java.lang.Math.{PI, exp, pow, sqrt}

object Other {

  def gaussian_pdf(mean: Double, sigma: Double)(x: DenseVector[Double]): Double =
    exp((-1)*pow(x(0)-mean, 2)/(2*pow(sigma, 2)))/sqrt(2*pow(sigma,2)*PI)

  def gaussian_pdf_d(mean: Double, sigma: Double)(x: DenseVector[Double]):  DenseVector[Double] =
    DenseVector((x(0)-mean)*gaussian_pdf(mean, sigma)(x)/pow(sigma, 2))

  //def kde(x: DenseVector[Double]) : Double

}
