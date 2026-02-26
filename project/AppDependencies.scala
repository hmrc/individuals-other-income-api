import sbt.*

object AppDependencies {

  val bootstrapPlayVersion = "9.19.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "org.typelevel"     %% "cats-core"                 % "2.13.0",
    "com.github.jknack"  % "handlebars"                % "4.3.1",
    "com.neovisionaries" % "nv-i18n"                   % "1.29"
  )

  val test: Seq[sbt.ModuleID] = Seq(
    "org.scalatestplus" %% "scalacheck-1-18"        % "3.2.19.0",
    "org.scalamock"     %% "scalamock"              % "7.4.0",
    "uk.gov.hmrc"       %% "bootstrap-test-play-30" % bootstrapPlayVersion
  ).map(_ % Test)

  val itDependencies: Seq[ModuleID] = Seq(
    "io.swagger.parser.v3"          % "swagger-parser-v3"    % "2.1.30",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.19.1"
  ).map(_ % Test)

}
