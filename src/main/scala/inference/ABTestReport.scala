package inference

case class ABTestReport(test: BayesianABTest) {
  override def toString: String = "Test"
}
