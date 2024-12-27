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

package v1.controllers

import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.TaxYear
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.routing.Version1
import shared.services.MockAuditService
import v1.controllers.validators.MockCreateAmendOtherValidatorFactory
import v1.fixtures.other.CreateAmendOtherFixtures.{requestBodyJson, requestBodyModel}
import v1.mocks.services.MockCreateAmendOtherService
import v1.models.request.createAmendOther._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendOtherControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreateAmendOtherValidatorFactory
    with MockAuditService
    with MockCreateAmendOtherService
    with MockSharedAppConfig {

  val taxYear: String = "2019-20"

  val requestData: CreateAmendOtherRequest = CreateAmendOtherRequest(
    nino = parsedNino,
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestBodyModel
  )

  "CreateAmendOtherController" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockCreateAmendOtherService
          .createAmend(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = None,
          maybeAuditResponseBody = None
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, Some(requestBodyJson))
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockCreateAmendOtherService
          .createAmend(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, Some(requestBodyJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new CreateAmendOtherController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreateAmendOtherValidatorFactory,
      service = mockCreateAmendOtherService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.createAmendOther(validNino, taxYear)(fakeRequest.withBody(requestBodyJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreateAmendOtherIncome",
        transactionName = "create-amend-other-income",
        detail = GenericAuditDetail(
          userType = "Individual",
          agentReferenceNumber = None,
          versionNumber = Version1.name,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
