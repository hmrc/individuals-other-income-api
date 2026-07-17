/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v3.retrieveOther

import api.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import api.models.errors.*
import api.utils.UnitSpec
import cats.data.Validated.{Invalid, Valid}
import api.config.MockAppConfig
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import v3.retrieveOther.RetrieveOtherSchema.*

class RetrieveOtherSchemaSpec extends UnitSpec with MockAppConfig with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  private trait Test {
    MockedAppConfig.minimumPermittedTaxYear(2020).anyNumberOfTimes()
  }

  "schema lookup" when {
    "a valid tax year is supplied" must {
      "use Def1 schema for tax years between 2020-21 and 2022-23" in new Test {
        forTaxYearsInRange(TaxYear.fromMtd("2020-21"), TaxYear.fromMtd("2022-23")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def1)
        }
      }

      "use Def2 schema for tax years 2023-24 onwards" in new Test {
        forTaxYearsFrom(TaxYear.fromMtd("2023-24")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def2)
        }
      }

    }

    "handle errors" when {
      "an invalid tax year is supplied" must {
        "disallow tax years prior to 2020-21 and return RuleTaxYearNotSupportedError" in new Test {
          forTaxYearsBefore(TaxYear.fromMtd("2019-20")) { taxYear =>
            schemaFor(taxYear.asMtd) shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
          }
        }
      }

      "the tax year format is invalid" must {
        "return a TaxYearFormatError" in new Test {
          schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
        }

        "the tax year range is invalid" must {
          "return a RuleTaxYearRangeInvalidError" in new Test {
            schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
          }
        }
      }
    }
  }

}
