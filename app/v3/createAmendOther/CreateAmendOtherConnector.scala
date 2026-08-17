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

import api.config.{AppConfig, ConfigFeatureSwitches}
import api.connectors.DownstreamUri.{HipUri, IfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser.*
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v3.createAmendOther.model.request.CreateAmendOtherRequestData
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendOtherConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def createAmend(request: CreateAmendOtherRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request.*

    val url = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1915") && taxYear.year >= 2026) {
      HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/other/$nino")
    } else {
      IfsUri[Unit](s"income-tax/income/other/${taxYear.asTysDownstream}/$nino")
    }

    put(body, url)
  }

}
