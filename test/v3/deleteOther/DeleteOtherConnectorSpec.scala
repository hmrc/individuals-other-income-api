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

package v3.deleteOther

import api.config.MockAppConfig
import api.connectors.ConnectorSpec
import api.mocks.MockHttpClient
import api.models.domain.{Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v3.deleteOther.def1.model.request.Def1_DeleteOtherRequest
import v3.deleteOther.model.request.DeleteOtherRequestData

import scala.concurrent.Future

class DeleteOtherConnectorSpec extends ConnectorSpec {

  "DeleteOtherConnector" should {
    "return the expected response for a TYS request" when {
      "a valid request is made" in new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2025-26")
        val outcome          = Right(ResponseWrapper(correlationId, ()))

        willDelete(
          url = url"$baseUrl/income-tax/income/other/25-26/$nino"
        ).returns(Future.successful(outcome))

        await(connector.deleteOther(request)) shouldBe outcome
      }
    }
  }

  trait Test extends ConnectorTest with MockAppConfig with MockHttpClient {

    def taxYear: TaxYear

    protected val nino: String = "AA111111A"

    protected val request: DeleteOtherRequestData =
      Def1_DeleteOtherRequest(
        nino = Nino(nino),
        taxYear = taxYear
      )

    val connector: DeleteOtherConnector = new DeleteOtherConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

}
