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

import common.RuleUnalignedCessationTaxYear
import play.api.Configuration
import shared.config.MockSharedAppConfig
import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v1.fixtures.other.CreateAmendOtherFixtures._
import v1.mocks.connectors.MockCreateAmendOtherConnector
import v1.models.request.createAmendOther.CreateAmendOtherRequest

import scala.concurrent.Future

class CreateAmendOtherServiceSpec extends ServiceSpec {

  private val nino    = "ZG903729C"
  private val taxYear = "2019-20"

  trait Test extends MockCreateAmendOtherConnector with MockSharedAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("Other", "createAmend")

    val createAmendOtherRequest: CreateAmendOtherRequest = CreateAmendOtherRequest(
      Nino(nino),
      TaxYear.fromMtd(taxYear),
      requestBodyModel
    )

    val service: CreateAmendOtherService = new CreateAmendOtherService(
      connector = mockCreateAmendOtherConnector,
      appConfig = mockSharedAppConfig
    )

  }

  "CreateAmendOtherService" when {
    "createAmend" must {
      "return correct result for a success with PCR feature switch enabled" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockCreateAmendOtherConnector
          .createAmendOther(createAmendOtherRequest)
          .returns(Future.successful(outcome))

        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> true))

        await(service.createAmend(createAmendOtherRequest)) shouldBe outcome
      }

      "return correct result for a success with PCR feature switch disabled" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        override val createAmendOtherRequest: CreateAmendOtherRequest = CreateAmendOtherRequest(
          Nino(nino),
          TaxYear.fromMtd(taxYear),
          requestBodyModel.copy(postCessationReceipts = None)
        )
        MockCreateAmendOtherConnector
          .createAmendOther(createAmendOtherRequest)
          .returns(Future.successful(outcome))

        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> false))

        await(service.createAmend(createAmendOtherRequest)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockCreateAmendOtherConnector
              .createAmendOther(createAmendOtherRequest)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> true))

            await(service.createAmend(createAmendOtherRequest)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATIONID", InternalError),
          ("INVALID_PAYLOAD", InternalError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError),
          ("UNALIGNED_CESSATION_TAX_YEAR", RuleUnalignedCessationTaxYear)
        )

        val extraTysErrors = List(
          ("INVALID_CORRELATION_ID", InternalError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
