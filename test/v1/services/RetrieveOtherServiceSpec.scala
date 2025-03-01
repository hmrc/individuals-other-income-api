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

package v1.services

import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear, Timestamp}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.connectors.MockRetrieveOtherConnector
import v1.models.request.retrieveOther.RetrieveOtherRequest
import v1.models.response.retrieveOther.RetrieveOtherResponse

import scala.concurrent.Future

class RetrieveOtherServiceSpec extends ServiceSpec {

  "RetrieveOtherService" should {
    "return the expected response for a non-TYS request" when {
      "a valid request is made" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[RetrieveOtherResponse]] = Right(ResponseWrapper(correlationId, response))

        MockRetrieveOtherConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockRetrieveOtherConnector
              .retrieve(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATIONID", InternalError),
          ("NO_DATA_FOUND", NotFoundError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError)
        )

        val extraTysErrors = List(
          ("INVALID_CORRELATION_ID", InternalError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

  trait Test extends MockRetrieveOtherConnector {

    private val nino    = "AA112233A"
    private val taxYear = "2019-20"

    val request: RetrieveOtherRequest = RetrieveOtherRequest(
      nino = Nino(nino),
      taxYear = TaxYear.fromMtd(taxYear)
    )

    val response: RetrieveOtherResponse = RetrieveOtherResponse(
      submittedOn = Timestamp("2019-04-04T01:01:01.000Z"),
      postCessationReceipts = None,
      businessReceipts = None,
      allOtherIncomeReceivedWhilstAbroad = None,
      overseasIncomeAndGains = None,
      chargeableForeignBenefitsAndGifts = None,
      omittedForeignIncome = None
    )

    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "RetrieveOther")

    val service: RetrieveOtherService = new RetrieveOtherService(
      connector = mockRetrieveOtherConnector
    )

  }

}
