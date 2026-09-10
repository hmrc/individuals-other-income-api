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

package v3.createAmendOther

import api.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import api.models.errors.*
import api.utils.UnitSpec
import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import v3.createAmendOther.CreateAmendOtherSchema.*

class CreateAmendOtherSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  private val minimumTaxYear: TaxYear = TaxYear.fromMtd("2025-26")

  "schema lookup" when {
    "a valid tax year is supplied" should {
      "use Def1 schema for tax year 2025-26" in {
        schemaFor(minimumTaxYear.asMtd) shouldBe Valid(Def1)
      }

      "use Def2 schema for tax years 2026-27 onwards" in {
        forTaxYearsFrom(TaxYear.fromMtd("2026-27")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def2)
        }
      }

    }

    "handle errors" when {
      "an unsupported tax year is supplied" should {
        "return RuleTaxYearNotSupportedError" in {
          forTaxYearsBefore(minimumTaxYear) { taxYear =>
            schemaFor(taxYear.asMtd) shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
          }
        }
      }

      "the tax year format is invalid" should {
        "return TaxYearFormatError" in {
          schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
        }
      }

      "the tax year range is invalid" should {
        "return RuleTaxYearRangeInvalidError" in {
          schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
        }
      }
    }
  }

}
