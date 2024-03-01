/*
 * Copyright 2023 HM Revenue & Customs
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

package api.controllers

import api.controllers.requestParsers.RequestParser
import api.mocks.MockIdGenerator
import api.mocks.services.MockAuditService
import api.models.audit.{AuditError, AuditEvent, AuditResponse, GenericAuditDetail}
import org.scalamock.handlers.CallHandler
import play.api.libs.json.JsString
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import api.models.auth.UserDetails
import api.models.errors.{ErrorWrapper, NinoFormatError}
import api.models.outcomes.ResponseWrapper
import api.models.request.RawData
import api.services.ServiceOutcome
import play.api.http.{HeaderNames, Status}
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.AnyContent
import play.api.test.{FakeRequest, ResultExtractors}
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class RequestHandlerSpec extends UnitSpec with MockAuditService with MockIdGenerator with Status with HeaderNames with ResultExtractors {

  private val successResponseJson = Json.obj("result" -> "SUCCESS!")
  private val successCode         = Status.ACCEPTED

  private val generatedCorrelationId = "generatedCorrelationId"
  private val serviceCorrelationId   = "serviceCorrelationId"

  case object InputRaw extends RawData
  case object Input
  case object Output { implicit val writes: OWrites[Output.type] = _ => successResponseJson }

  private val userDetails                           = UserDetails("mtdId", "Individual", Some("agentReferenceNumber"))
  implicit val userRequest: UserRequest[AnyContent] = UserRequest[AnyContent](userDetails, FakeRequest())

  trait DummyService {
    def service(input: Input.type)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Output.type]]
  }

  private val mockService = mock[DummyService]

  private def service =
    (mockService.service(_: Input.type)(_: RequestContext, _: ExecutionContext)).expects(Input, *, *)

  private val mockParser = mock[RequestParser[InputRaw.type, Input.type]]

  private def parseRequest =
    (mockParser.parseRequest(_: InputRaw.type)(_: String)).expects(InputRaw, *)

  private def auditResult(headerCarrier: HeaderCarrier, response: Boolean, params: Map[String, String], auditType: String, txName: String, requestBody: Some[JsString]) = {
    val generatedCorrelationId = "generatedCorrelationId"
    MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()

    implicit val hc: HeaderCarrier = headerCarrier

    implicit val endpointLogContext: EndpointLogContext =
      EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

    implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

    def auditHandler(includeResponse: Boolean = response): AuditHandler = AuditHandler(
      mockAuditService,
      auditType = auditType,
      transactionName = txName,
      params = params,
      requestBody = requestBody,
      includeResponse = includeResponse
    )

    val basicRequestHandler = RequestHandler
      .withParser(mockParser)
      .withService(mockService.service)
      .withPlainJsonResult(successCode)

    basicRequestHandler.withAuditing(auditHandler())
  }

  private def verifyAudit(correlationId: String, auditResponse: AuditResponse, params: Map[String, String], auditType: String, txName: String, requestBody: Some[JsString]): CallHandler[Future[AuditResult]] =
    MockedAuditService.verifyAuditEvent(AuditEvent(
      auditType = auditType,
      transactionName = txName,
      GenericAuditDetail(userDetails, params = params, request = requestBody, `X-CorrelationId` = correlationId, auditResponse)
    ))

  "RequestHandler" when {
    "a request is successful" must {
      "return the correct response" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withParser(mockParser)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        parseRequest returns Right(Input)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest(InputRaw)

        contentAsJson(result) shouldBe successResponseJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe successCode
      }

      "return no content if required" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withParser(mockParser)
          .withService(mockService.service)
          .withNoContentResult()

        parseRequest returns Right(Input)
        service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

        val result = requestHandler.handleRequest(InputRaw)

        contentAsString(result) shouldBe ""
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NO_CONTENT
      }
    }

    "a request fails with validation errors" must {
      "return the errors" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withParser(mockParser)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        parseRequest returns Left(ErrorWrapper(generatedCorrelationId, NinoFormatError))

        val result = requestHandler.handleRequest(InputRaw)

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "a request fails with service errors" must {
      "return the errors" in new nonGTSTest {
        val requestHandler = RequestHandler
          .withParser(mockParser)
          .withService(mockService.service)
          .withPlainJsonResult(successCode)

        parseRequest returns Right(Input)
        service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

        val result = requestHandler.handleRequest(InputRaw)

        contentAsJson(result) shouldBe NinoFormatError.asJson
        header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
        status(result) shouldBe NinoFormatError.httpStatus
      }
    }

    "a request has a REQUEST_CANNOT_BE_FULFILLED gov-test-scenario heading" must {
      "return RULE_REQUEST_CANNOT_BE_FULFILLED" in new GTSTest {
        val params = Map("param" -> "value")

        val auditType = "type"
        val txName = "txName"

        val requestBody = Some(JsString("REQUEST BODY"))

        val requestHandler = auditResult(HeaderCarrier(otherHeaders = Seq(("Gov-Test-Scenario", "REQUEST_CANNOT_BE_FULFILLED"))), response = true, params, auditType, txName, requestBody)
        val result = requestHandler.handleRequest(InputRaw)

        status(result) shouldBe 422
        contentAsJson(result) shouldBe Json.toJson("Custom (will vary depending on the actual error)")
      }
    }

  }

  "RequestHandler" when {
    "auditing is configured" when {

      "a request is successful" when {
        "no response is to be audited" must {
          "audit without the response" in new nonGTSTest {
            val params = Map("param" -> "value")

            val auditType = "type"
            val txName = "txName"

            val requestBody = Some(JsString("REQUEST BODY"))

            val requestHandler = auditResult(HeaderCarrier(), false, params, auditType, txName, requestBody)
            parseRequest returns Right(Input)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))
            val result = requestHandler.handleRequest(InputRaw)

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(None)), params, auditType, txName, requestBody)
          }
        }

        "the response is to be audited" must {
          "audit with the response" in new nonGTSTest {
            val params = Map("param" -> "value")

            val auditType = "type"
            val txName = "txName"

            val requestBody = Some(JsString("REQUEST BODY"))

            val requestHandler = auditResult(HeaderCarrier(), response = true, params, auditType, txName, requestBody)

            parseRequest returns Right(Input)
            service returns Future.successful(Right(ResponseWrapper(serviceCorrelationId, Output)))

            val result = requestHandler.handleRequest(InputRaw)

            contentAsJson(result) shouldBe successResponseJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe successCode

            verifyAudit(serviceCorrelationId, AuditResponse(successCode, Right(Some(successResponseJson))),params, auditType, txName, requestBody)
          }
        }
      }


      "a request fails with validation errors" must {
        "audit the failure" in new nonGTSTest {
          val params = Map("param" -> "value")

          val auditType = "type"
          val txName = "txName"

          val requestBody = Some(JsString("REQUEST BODY"))

          val requestHandler = auditResult(HeaderCarrier(), response = false, params, auditType, txName, requestBody)
          parseRequest returns Left(ErrorWrapper(generatedCorrelationId, NinoFormatError))

          val result = requestHandler.handleRequest(InputRaw)

          contentAsJson(result) shouldBe NinoFormatError.asJson
          header("X-CorrelationId", result) shouldBe Some(generatedCorrelationId)
          status(result) shouldBe NinoFormatError.httpStatus

          verifyAudit(generatedCorrelationId, AuditResponse(NinoFormatError.httpStatus, Left(Seq(AuditError(NinoFormatError.code)))),params, auditType, txName, requestBody)
        }


        "a request fails with service errors" must {
          "audit the failure" in new nonGTSTest {
            val params = Map("param" -> "value")

            val auditType = "type"
            val txName = "txName"

            val requestBody = Some(JsString("REQUEST BODY"))

            val requestHandler = auditResult(HeaderCarrier(), response = false, params, auditType, txName, requestBody)
            parseRequest returns Right(Input)
            service returns Future.successful(Left(ErrorWrapper(serviceCorrelationId, NinoFormatError)))

            val result = requestHandler.handleRequest(InputRaw)

            contentAsJson(result) shouldBe NinoFormatError.asJson
            header("X-CorrelationId", result) shouldBe Some(serviceCorrelationId)
            status(result) shouldBe NinoFormatError.httpStatus

            verifyAudit(serviceCorrelationId, AuditResponse(NinoFormatError.httpStatus, Left(Seq(AuditError(NinoFormatError.code)))), params, auditType, txName, requestBody)
          }
        }
      }
    }
  }

}

trait nonGTSTest extends MockIdGenerator {
  private val generatedCorrelationId = "generatedCorrelationId"
  MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()
  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

  implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

}

trait GTSTest extends MockIdGenerator {
  private val generatedCorrelationId = "generatedCorrelationId"
  MockIdGenerator.generateCorrelationId.returns(generatedCorrelationId).anyNumberOfTimes()

  implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = Seq(("Gov-Test-Scenario", "REQUEST_CANNOT_BE_FULFILLED")))

  implicit val endpointLogContext: EndpointLogContext =
    EndpointLogContext(controllerName = "SomeController", endpointName = "someEndpoint")

  implicit val ctx: RequestContext = RequestContext.from(mockIdGenerator, endpointLogContext)

}
