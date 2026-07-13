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

import api.config.MockAppConfig
import api.controllers.EndpointLogContext
import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import play.api.Configuration
import v3.createAmend.def1.fixtures.Def1_CreateAmendOtherFixtures.requestBodyModel
import v3.createAmend.def1.model.request.Def1_CreateAmendOtherRequestData
import v3.mocks.connectors.MockCreateAmendOtherConnector

import scala.concurrent.Future

class CreateAmendOtherServiceSpec extends ServiceSpec {

  private val nino    = "ZG903729C"
  private val taxYear = "2025-26"

  trait Test extends MockCreateAmendOtherConnector with MockAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("Other", "createAmend")

    val def1CreateAmendOtherRequestData: Def1_CreateAmendOtherRequestData = Def1_CreateAmendOtherRequestData(
      Nino(nino),
      TaxYear.fromMtd(taxYear),
      requestBodyModel
    )

    val service: CreateAmendOtherService = new CreateAmendOtherService(
      connector = mockCreateAmendOtherConnector
    )

  }

  "CreateAmendOtherService" when {
    "createAmend" must {
      "return correct result for a success with PCR feature switch enabled" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockCreateAmendOtherConnector
          .createAmendOther(def1CreateAmendOtherRequestData)
          .returns(Future.successful(outcome))

        MockedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> true))

        await(service.createAmend(def1CreateAmendOtherRequestData)) shouldBe outcome
      }

      "return correct result for a success with PCR feature switch disabled" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        override val def1CreateAmendOtherRequestData: Def1_CreateAmendOtherRequestData = Def1_CreateAmendOtherRequestData(
          Nino(nino),
          TaxYear.fromMtd(taxYear),
          requestBodyModel.copy(postCessationReceipts = None)
        )
        MockCreateAmendOtherConnector
          .createAmendOther(def1CreateAmendOtherRequestData)
          .returns(Future.successful(outcome))

        MockedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> false))

        await(service.createAmend(def1CreateAmendOtherRequestData)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockCreateAmendOtherConnector
              .createAmendOther(def1CreateAmendOtherRequestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            MockedAppConfig.featureSwitchConfig.returns(Configuration("postCessationReceipts.enabled" -> true))

            await(service.createAmend(def1CreateAmendOtherRequestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATION_ID", InternalError),
          ("INVALID_PAYLOAD", InternalError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError),
          ("UNALIGNED_CESSATION_TAX_YEAR", RuleUnalignedCessationTaxYearError),
          ("OUTSIDE_AMENDMENT_WINDOW", RuleOutsideAmendmentWindowError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        errors.foreach(args => serviceError.tupled(args))
      }
    }
  }

}
