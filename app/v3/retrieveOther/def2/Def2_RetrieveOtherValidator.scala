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

package v3.retrieveOther.def2

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.ResolveNino
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import v3.retrieveOther.def2.model.request.Def2_RetrieveOtherRequestData
import v3.retrieveOther.model.request.RetrieveOtherRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class Def2_RetrieveOtherValidator @Inject() (nino: String, taxYear: String) extends Validator[RetrieveOtherRequestData] {

  override def validate: Validated[Seq[MtdError], Def2_RetrieveOtherRequestData] =
    ResolveNino(nino).map(validNino => Def2_RetrieveOtherRequestData(validNino, TaxYear.fromMtd(taxYear)))

}
