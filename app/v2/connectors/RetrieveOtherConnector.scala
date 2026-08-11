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

package v2.connectors

import api.config.{AppConfig, ConfigFeatureSwitches}
import api.connectors.DownstreamUri.{DesUri, HipUri, IfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser.reads
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v2.models.request.retrieveOther.RetrieveOtherRequest
import v2.models.response.retrieveOther.RetrieveOtherResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveOtherConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieve(request: RetrieveOtherRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveOtherResponse]] = {

    import request.*

    lazy val downstream1916Uri = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1916")) {
      HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/other/${nino.value}")
    } else {
      IfsUri(s"income-tax/income/other/${taxYear.asTysDownstream}/${nino.value}")
    }

    lazy val downstream1621Uri = DesUri(s"income-tax/income/other/${nino.value}/${taxYear.asMtd}")

    val downstreamUri: DownstreamUri[RetrieveOtherResponse] = if (taxYear.useTaxYearSpecificApi) downstream1916Uri else downstream1621Uri

    get(downstreamUri)
  }

}
