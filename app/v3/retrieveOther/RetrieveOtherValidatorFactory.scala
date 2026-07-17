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

import api.config.AppConfig
import api.controllers.validators.Validator
import cats.data.Validated.{Invalid, Valid}
import v3.retrieveOther.RetrieveOtherSchema.{Def1, Def2}
import v3.retrieveOther.def1.Def1_RetrieveOtherValidator
import v3.retrieveOther.def2.Def2_RetrieveOtherValidator
import v3.retrieveOther.model.request.RetrieveOtherRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class RetrieveOtherValidatorFactory @Inject() (implicit appConfig: AppConfig) {

  def validator(nino: String, taxYear: String): Validator[RetrieveOtherRequestData] = {
    val schema = RetrieveOtherSchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_RetrieveOtherValidator(nino, taxYear)
      case Valid(Def2)     => new Def2_RetrieveOtherValidator(nino, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
