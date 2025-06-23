import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object AppDependencies {

  val bootstrapPlayVersion = "9.13.0"

  val compile: Seq[ModuleID] = List(
    ws,
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "org.typelevel"                %% "cats-core"                 % "2.13.0",
    "com.chuusai"                  %% "shapeless"                 % "2.4.0-M1",
    "com.neovisionaries"            % "nv-i18n"                   % "1.29",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.19.1",
    "com.github.jknack"             % "handlebars"                % "4.3.1"
  )

  def test(scope: String = "test, it"): Seq[sbt.ModuleID] = List(
    "org.scalatestplus"   %% "scalacheck-1-15"        % "3.2.11.0"           % scope,
    "org.scalamock"       %% "scalamock"              % "7.3.2"              % scope,
    "uk.gov.hmrc"         %% "bootstrap-test-play-30" % bootstrapPlayVersion % scope,
    "io.swagger.parser.v3" % "swagger-parser-v3"      % "2.1.29"             % scope
  )

}
