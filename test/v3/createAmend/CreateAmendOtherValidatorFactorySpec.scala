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

package v3.createAmend

import api.config.MockAppConfig
import api.controllers.validators.{AlwaysErrorsValidator, Validator}
import api.utils.UnitSpec
import play.api.libs.json.*
import v3.createAmend.def1.Def1_CreateAmendOtherValidator
import v3.createAmend.def1.model.request.Def1_CreateAmendOtherRequestData

class CreateAmendOtherValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private val validNino    = "AA123456B"
  private val validTaxYear = "2025-26"
  private val validBody    = JsObject.empty

  private val validatorFactory = new CreateAmendOtherValidatorFactory

  "validator" when {
    "given a valid taxYear" should {
      "return the validator for schema definition 1" in new SetupConfig {
        val result: Validator[Def1_CreateAmendOtherRequestData] =
          validatorFactory.validator(validNino, validTaxYear, validBody)

        result shouldBe a[Def1_CreateAmendOtherValidator]
      }

      "return an error when given an invalid taxYear" in new SetupConfig {
        val result: Validator[Def1_CreateAmendOtherRequestData] =
          validatorFactory.validator(validNino, "2021-22", validBody)

        result shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
