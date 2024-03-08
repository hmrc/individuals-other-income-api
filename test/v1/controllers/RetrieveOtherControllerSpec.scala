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

package v1.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.models.domain.{Nino, TaxYear, Timestamp}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.mvc.Result
import v1.fixtures.RetrieveOtherControllerFixture.fullRetrieveOtherResponse
import v1.mocks.requestParsers.MockRetrieveOtherRequestParser
import v1.mocks.services.MockRetrieveOtherService
import v1.models.request.retrieveOther.{RetrieveOtherRawData, RetrieveOtherRequest}
import v1.models.response.retrieveOther._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveOtherControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrieveOtherService
    with MockRetrieveOtherRequestParser
    with MockAppConfig {

  val taxYear: String = "2019-20"

  val rawData: RetrieveOtherRawData = RetrieveOtherRawData(
    nino = nino,
    taxYear = taxYear
  )

  val requestData: RetrieveOtherRequest = RetrieveOtherRequest(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  private val postCessationReceiptsItemModel = List(
    PostCessationReceiptsItem(
      customerReference = Some("String"),
      businessName = Some("LsMBEqEWnG9j,9JP9RpgkGmIcF2I30.NpxZRtgN3zA7-b8h-LvHvApdJtpY"),
      dateBusinessCeased = Some("2023-06-01"),
      businessDescription = Some("u2e'VarLXLa\\W&RHojlOZIqm9NDG"),
      incomeSource = Some("string"),
      amount = 99999999999.99,
      taxYearIncomeToBeTaxed = "2019-20"
    )
  )

  private val businessReceiptsItemModel = List(
    BusinessReceiptsItem(
      grossAmount = 5000.99,
      taxYear = "2018-19"
    ),
    BusinessReceiptsItem(
      grossAmount = 6000.99,
      taxYear = "2019-20"
    )
  )

  private val allOtherIncomeReceivedWhilstAbroadItemModel = List(
    AllOtherIncomeReceivedWhilstAbroadItem(
      countryCode = "FRA",
      amountBeforeTax = Some(1999.99),
      taxTakenOff = Some(2.23),
      specialWithholdingTax = Some(3.23),
      foreignTaxCreditRelief = None,
      taxableAmount = 4.23,
      residentialFinancialCostAmount = Some(2999.99),
      broughtFwdResidentialFinancialCostAmount = Some(1999.99)
    ),
    AllOtherIncomeReceivedWhilstAbroadItem(
      countryCode = "IND",
      amountBeforeTax = Some(2999.99),
      taxTakenOff = Some(3.23),
      specialWithholdingTax = Some(4.23),
      foreignTaxCreditRelief = Some(true),
      taxableAmount = 5.23,
      residentialFinancialCostAmount = Some(3999.99),
      broughtFwdResidentialFinancialCostAmount = Some(2999.99)
    )
  )

  private val overseasIncomeAndGainsModel = OverseasIncomeAndGains(gainAmount = 3000.99)

  private val chargeableForeignBenefitsAndGiftsModel = ChargeableForeignBenefitsAndGifts(
    transactionBenefit = Some(1999.99),
    protectedForeignIncomeSourceBenefit = Some(2999.99),
    protectedForeignIncomeOnwardGift = Some(3999.99),
    benefitReceivedAsASettler = Some(4999.99),
    onwardGiftReceivedAsASettler = Some(5999.99)
  )

  private val omittedForeignIncomeModel = OmittedForeignIncome(amount = 4000.99)

  private val retrieveOtherResponseModel = RetrieveOtherResponse(
    submittedOn = Timestamp("2019-04-04T01:01:01.000Z"),
    Some(postCessationReceiptsItemModel),
    Some(businessReceiptsItemModel),
    Some(allOtherIncomeReceivedWhilstAbroadItemModel),
    Some(overseasIncomeAndGainsModel),
    Some(chargeableForeignBenefitsAndGiftsModel),
    Some(omittedForeignIncomeModel)
  )

  private val mtdResponse = fullRetrieveOtherResponse

  "RetrieveOtherController" should {
    "return OK" when {
      "the request is valid" in new Test {
        MockRetrieveOtherRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveOtherService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveOtherResponseModel))))

        runOkTest(expectedStatus = OK, maybeExpectedResponseBody = Some(mtdResponse))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockRetrieveOtherRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError, None)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockRetrieveOtherRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveOtherService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest {

    val controller = new RetrieveOtherController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveOtherRequestParser,
      service = mockRetrieveOtherService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.retrieveOther(nino, taxYear)(fakeGetRequest)

  }

}
