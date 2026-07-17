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

import api.controllers.validators.{AlwaysErrorsValidator, Validator}
import api.config.MockAppConfig
import api.utils.UnitSpec
import v3.retrieveOther.def1.Def1_RetrieveOtherValidator
import v3.retrieveOther.def2.Def2_RetrieveOtherValidator
import v3.retrieveOther.model.request.RetrieveOtherRequestData

class RetrieveOtherValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private def validatorFor(taxYear: String): Validator[RetrieveOtherRequestData] =
    new RetrieveOtherValidatorFactory().validator(nino = "ignoredNino", taxYear = taxYear)

  private trait Test {

    MockedAppConfig.minimumPermittedTaxYear(2020).anyNumberOfTimes()
  }

  "RetrieveOtherValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in new Test {
        validatorFor("2022-23") shouldBe a[Def1_RetrieveOtherValidator]
      }
    }
    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in new Test {
        validatorFor("2025-26") shouldBe a[Def2_RetrieveOtherValidator]
      }
    }
    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in new Test {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
