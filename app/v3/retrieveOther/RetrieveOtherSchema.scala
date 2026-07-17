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
import api.controllers.validators.resolvers.ResolveTaxYearMinimum
import api.models.domain.TaxYear
import api.models.errors.MtdError
import api.schema.DownstreamReadable
import cats.data.Validated
import cats.data.Validated.Valid
import play.api.libs.json.Reads
import v3.retrieveOther.def1.model.response.Def1_RetrieveOtherResponse
import v3.retrieveOther.def2.model.response.Def2_RetrieveOtherResponse
import v3.retrieveOther.model.response.RetrieveOtherResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveOtherSchema extends DownstreamReadable[RetrieveOtherResponse]

object RetrieveOtherSchema {

  case object Def1 extends RetrieveOtherSchema {
    type DownstreamResp = Def1_RetrieveOtherResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveOtherResponse.reads
  }

  case object Def2 extends RetrieveOtherSchema {
    type DownstreamResp = Def2_RetrieveOtherResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveOtherResponse.reads
  }

  def schemaFor(taxYearString: String)(implicit appConfig: AppConfig): Validated[Seq[MtdError], RetrieveOtherSchema] =
    ResolveTaxYearMinimum(TaxYear.ending(appConfig.minimumPermittedTaxYear))(taxYearString) andThen schemaFor

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveOtherSchema] = {
    if (taxYear <= TaxYear.fromMtd("2022-23")) {
      Valid(Def1)
    } else {
      Valid(Def2)
    }
  }

}
