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

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v1.models.request.createAmendOther.{CreateAmendOtherRequest, CreateAmendOtherRequestBody}

object CreateAmendOtherValidator {

  private val resolveJson = ResolveNonEmptyJsonObject.resolver[CreateAmendOtherRequestBody]

}

class CreateAmendOtherValidator(nino: String, taxYear: String, body: JsValue) extends Validator[CreateAmendOtherRequest] {
  import CreateAmendOtherValidator._

  private val resolveTaxYear = ResolveTaxYearMinimum(TaxYear.fromMtd("2019-20"))

  override def validate: Validated[Seq[MtdError], CreateAmendOtherRequest] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(CreateAmendOtherRequest) andThen CreateAmendOtherRulesValidator.validateBusinessRules

}
