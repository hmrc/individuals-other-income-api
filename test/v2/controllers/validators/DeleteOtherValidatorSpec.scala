/*
 * Copyright 2024 HM Revenue & Customs
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

package v2.controllers.validators

import shared.config.MockSharedAppConfig
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v2.models.request.deleteOther.DeleteOtherRequest

class DeleteOtherValidatorSpec extends UnitSpec with JsonErrorValidators with MockSharedAppConfig {

  private implicit val correlationId: String = "correlationId"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2019-20"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  def validator(nino: String = validNino, taxYear: String = validTaxYear) =
    new DeleteOtherValidator(nino, taxYear)(mockSharedAppConfig)

  def validate(nino: String = validNino, taxYear: String = validTaxYear) =
    validator(nino, taxYear).validateAndWrapResult()

  def singleError(error: MtdError): Left[ErrorWrapper, Nothing] = Left(ErrorWrapper(correlationId, error))

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in new SetupConfig {
        validate(validNino, validTaxYear) shouldBe Right(DeleteOtherRequest(parsedNino, parsedTaxYear))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new SetupConfig {
        validate("A12344A", validTaxYear) shouldBe singleError(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new SetupConfig {
        validate(validNino, "20178") shouldBe singleError(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an invalid tax year is supplied" in new SetupConfig {
        validate(validNino, "2017-18") shouldBe singleError(RuleTaxYearNotSupportedError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors (path parameters)" in new SetupConfig {
        validate("A12344A", "20178") shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
