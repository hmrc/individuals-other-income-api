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

package v1.controllers.validators

import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import config.AppConfig
import mocks.MockAppConfig
import support.UnitSpec
import v1.models.request.deleteOther.DeleteOtherRequest

class DeleteOtherValidatorSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private implicit val correlationId: String = "correlationId"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2019-20"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  class Test extends MockAppConfig {

    implicit val appConfig: AppConfig = mockAppConfig

    def validate(nino: String, taxYear: String): Either[ErrorWrapper, DeleteOtherRequest] =
      new DeleteOtherValidator(nino, taxYear, appConfig).validateAndWrapResult()

    def singleError(error: MtdError): Left[ErrorWrapper, Nothing] = Left(ErrorWrapper(correlationId, error))

    MockedAppConfig.minimumPermittedTaxYear returns TaxYear.ending(2020)
  }

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in new Test {
        validate(validNino, validTaxYear) shouldBe Right(DeleteOtherRequest(parsedNino, parsedTaxYear))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        validate("A12344A", validTaxYear) shouldBe singleError(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        validate(validNino, "20178") shouldBe singleError(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an invalid tax year is supplied" in new Test {
        validate(validNino, "2017-18") shouldBe singleError(RuleTaxYearNotSupportedError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors (path parameters)" in new Test {
        validate("A12344A", "20178") shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
