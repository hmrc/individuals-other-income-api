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

package v3.createAmend.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.*
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import v3.createAmend.def1.model.request.{Def1_CreateAmendOtherRequestBody, Def1_CreateAmendOtherRequestData}

class Def1_CreateAmendOtherValidator(nino: String, taxYear: String, body: JsValue) extends Validator[Def1_CreateAmendOtherRequestData] {

  private val resolveJson    = ResolveNonEmptyJsonObject.resolver[Def1_CreateAmendOtherRequestBody]
  private val rulesValidator = Def1_CreateAmendOtherRulesValidator

  override def validate: Validated[Seq[MtdError], Def1_CreateAmendOtherRequestData] =
    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN((validNino, validBody) =>
      Def1_CreateAmendOtherRequestData(validNino, TaxYear.fromMtd(taxYear), validBody)) andThen rulesValidator.validateBusinessRules

}
