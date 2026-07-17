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

import api.config.AppConfig
import api.controllers.validators.Validator
import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json.JsValue
import v3.createAmendOther.CreateAmendOtherSchema.Def1
import v3.createAmendOther.def1.Def1_CreateAmendOtherValidator
import v3.createAmendOther.def1.model.request.Def1_CreateAmendOtherRequestData
import javax.inject.{Inject, Singleton}

@Singleton
class CreateAmendOtherValidatorFactory @Inject() (implicit appConfig: AppConfig) {

  def validator(nino: String, taxYear: String, body: JsValue): Validator[Def1_CreateAmendOtherRequestData] =
    val schema = CreateAmendOtherSchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_CreateAmendOtherValidator(nino, taxYear, body)
      case Invalid(errors) => Validator.returningErrors(errors)
    }

}
