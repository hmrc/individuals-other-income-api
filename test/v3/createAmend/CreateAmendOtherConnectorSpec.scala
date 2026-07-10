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

import api.connectors.ConnectorSpec
import api.models.domain.{Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v3.createAmend.def1.model.request.Def1_CreateAmendOtherRequestData
import v3.createAmend.def1.fixtures.Def1_CreateAmendOtherFixtures.requestBodyModel

import scala.concurrent.Future

class CreateAmendOtherConnectorSpec extends ConnectorSpec {

  trait Test extends ConnectorTest {

    val taxYear: String

    val connector: CreateAmendOtherConnector = new CreateAmendOtherConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    lazy val def1CreateAmendOtherRequestData: Def1_CreateAmendOtherRequestData = Def1_CreateAmendOtherRequestData(
      nino = Nino("AA111111A"),
      taxYear = TaxYear.fromMtd(taxYear),
      body = requestBodyModel
    )

  }

  "CreateAmendOtherConnector" when {
    "createAmend" must {
      "return a 204 status for a success scenario" in new IfsTest with Test {
        val taxYear                                        = "2025-26"
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/other/${TaxYear.fromMtd(taxYear).asTysDownstream}/AA111111A",
          body = requestBodyModel
        )
          .returns(Future.successful(outcome))

        await(connector.createAmend(def1CreateAmendOtherRequestData)) shouldBe outcome
      }
    }
  }

}
