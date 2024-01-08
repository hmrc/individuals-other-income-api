import sbt.Setting
import scoverage.ScoverageKeys

object CodeCoverageSettings {

  val settings: Seq[Setting[_]] = List(
    ScoverageKeys.coverageExcludedPackages := excludedPackages.mkString(";"),
    ScoverageKeys.coverageExcludedFiles := ".*PensionsIncomePlayModule;.*DocumentationController",
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum    := true,
    ScoverageKeys.coverageHighlighting     := true
  )

  private val excludedPackages: Seq[String] = List(
    "<empty>",
    "Reverse.*",
    "uk.gov.hmrc.BuildInfo",
    "app.*",
    "prod.*",
    ".*Routes.*",
    "config.*",
    "testOnly.*",
    "testOnlyDoNotUseInAppConf.*"
  )

}
