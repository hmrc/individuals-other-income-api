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

package v2.endpoints

import common.{RuleOutsideAmendmentWindowError, RuleUnalignedCessationTaxYear}
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import v2.fixtures.other.CreateAmendOtherFixtures.{requestBodyJsonWithoutForeignTaxCreditRelief, requestBodyWithPCRJson}

class CreateAmendOtherControllerISpec extends IntegrationBaseSpec {

  "Calling the 'create and amend other income' endpoint" should {
    "return a 200 status code" when {
      "any valid request is made" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().put(requestBodyWithPCRJson))
        response.status shouldBe OK
        response.body shouldBe ""
      }

      "any valid request is made (TYS)" in new TysIfsTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().put(requestBodyWithPCRJson))
        response.status shouldBe OK
        response.body shouldBe ""
      }

      "any valid request is made (TYS) without foreignTaxCreditRelief" in new TysIfsTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().put(requestBodyJsonWithoutForeignTaxCreditRelief))
        response.status shouldBe OK
        response.body shouldBe ""
      }
    }

    "return a TaxYearFormatError" when {
      "a request body having invalid tax year format is supplied" in new NonTysTest {

        val invalidRequestBodyJson: JsValue = Json.parse(
          """
            |{
            |   "businessReceipts": [
            |      {
            |         "grossAmount": 5000.99,
            |         "taxYear": "2018-193"
            |      }
            |   ],
            |   "allOtherIncomeReceivedWhilstAbroad": [
            |      {
            |         "countryCode": "FRA",
            |         "amountBeforeTax": 1999.99,
            |         "taxTakenOff": 2.23,
            |         "specialWithholdingTax": 3.23,
            |         "foreignTaxCreditRelief": false,
            |         "taxableAmount": 4.23,
            |         "residentialFinancialCostAmount": 2999.99,
            |         "broughtFwdResidentialFinancialCostAmount": 1999.99
            |      },
            |      {
            |         "countryCode": "IND",
            |         "amountBeforeTax": 2999.99,
            |         "taxTakenOff": 3.23,
            |         "specialWithholdingTax": 4.23,
            |         "foreignTaxCreditRelief": true,
            |         "taxableAmount": 5.23,
            |         "residentialFinancialCostAmount": 3999.99,
            |         "broughtFwdResidentialFinancialCostAmount": 2999.99
            |      }
            |   ],
            |   "overseasIncomeAndGains": {
            |      "gainAmount": 3000.99
            |   },
            |   "chargeableForeignBenefitsAndGifts": {
            |      "transactionBenefit": 1999.99,
            |      "protectedForeignIncomeSourceBenefit": 2999.99,
            |      "protectedForeignIncomeOnwardGift": 3999.99,
            |      "benefitReceivedAsASettler": 4999.99,
            |      "onwardGiftReceivedAsASettler": 5999.99
            |   },
            |   "omittedForeignIncome": {
            |      "amount": 4000.99
            |   }
            |}
    """.stripMargin
        )

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().put(invalidRequestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(
          ErrorWrapper(
            correlationId = correlationId,
            error = TaxYearFormatError.copy(
              paths = Some(List("/businessReceipts/0/taxYear"))
            ),
            errors = None
          ))
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return a RuleTaxYearRangeInvalidError" when {
      "a request body having invalid tax year range is supplied" in new NonTysTest {

        val invalidRequestBodyJson: JsValue = Json.parse(
          """
            |{
            |   "businessReceipts": [
            |      {
            |         "grossAmount": 5000.99,
            |         "taxYear": "2018-23"
            |      }
            |   ],
            |   "allOtherIncomeReceivedWhilstAbroad": [
            |      {
            |         "countryCode": "FRA",
            |         "amountBeforeTax": 1999.99,
            |         "taxTakenOff": 2.23,
            |         "specialWithholdingTax": 3.23,
            |         "foreignTaxCreditRelief": false,
            |         "taxableAmount": 4.23,
            |         "residentialFinancialCostAmount": 2999.99,
            |         "broughtFwdResidentialFinancialCostAmount": 1999.99
            |      },
            |      {
            |         "countryCode": "IND",
            |         "amountBeforeTax": 2999.99,
            |         "taxTakenOff": 3.23,
            |         "specialWithholdingTax": 4.23,
            |         "foreignTaxCreditRelief": true,
            |         "taxableAmount": 5.23,
            |         "residentialFinancialCostAmount": 3999.99,
            |         "broughtFwdResidentialFinancialCostAmount": 2999.99
            |      }
            |   ],
            |   "overseasIncomeAndGains": {
            |      "gainAmount": 3000.99
            |   },
            |   "chargeableForeignBenefitsAndGifts": {
            |      "transactionBenefit": 1999.99,
            |      "protectedForeignIncomeSourceBenefit": 2999.99,
            |      "protectedForeignIncomeOnwardGift": 3999.99,
            |      "benefitReceivedAsASettler": 4999.99,
            |      "onwardGiftReceivedAsASettler": 5999.99
            |   },
            |   "omittedForeignIncome": {
            |      "amount": 4000.99
            |   }
            |}
    """.stripMargin
        )

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().put(invalidRequestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(
          ErrorWrapper(
            correlationId = correlationId,
            error = RuleTaxYearRangeInvalidError.copy(
              paths = Some(List("/businessReceipts/0/taxYear"))
            ),
            errors = None
          ))

        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return a 400 with multiple errors" when {
      "all field value validations fail on the request body" in new NonTysTest {

        val allInvalidValueRequestBodyJson: JsValue = Json.parse(
          """
            |{
            |   "businessReceipts":[
            |      {
            |         "grossAmount":5000.999,
            |         "taxYear":"2019"
            |      },
            |      {
            |         "grossAmount":6000.999,
            |         "taxYear":"2019-21"
            |      }
            |   ],
            |   "allOtherIncomeReceivedWhilstAbroad":[
            |      {
            |         "countryCode":"FRANCE",
            |         "amountBeforeTax":-1999.99,
            |         "taxTakenOff":-2.23,
            |         "specialWithholdingTax":3.233,
            |         "foreignTaxCreditRelief":false,
            |         "taxableAmount":4.233,
            |         "residentialFinancialCostAmount":-2999.99,
            |         "broughtFwdResidentialFinancialCostAmount":1999.995
            |      },
            |      {
            |         "countryCode":"SBT",
            |         "amountBeforeTax":-2999.99,
            |         "taxTakenOff":-3.23,
            |         "specialWithholdingTax":4.235,
            |         "foreignTaxCreditRelief":true,
            |         "taxableAmount":5.253,
            |         "residentialFinancialCostAmount":3999.959,
            |         "broughtFwdResidentialFinancialCostAmount":-2999.99
            |      }
            |   ],
            |   "overseasIncomeAndGains":{
            |      "gainAmount":3000.993
            |   },
            |   "chargeableForeignBenefitsAndGifts":{
            |      "transactionBenefit":1999.992,
            |      "protectedForeignIncomeSourceBenefit":2999.999,
            |      "protectedForeignIncomeOnwardGift":-3999.99,
            |      "benefitReceivedAsASettler":-4999.99,
            |      "onwardGiftReceivedAsASettler":5999.996
            |   },
            |   "omittedForeignIncome":{
            |      "amount":-4000.99
            |   }
            |}
            |""".stripMargin
        )

        val allInvalidValueRequestError: List[MtdError] = List(
          CountryCodeFormatError.copy(
            paths = Some(List("/allOtherIncomeReceivedWhilstAbroad/0/countryCode"))
          ),
          TaxYearFormatError.copy(
            paths = Some(List("/businessReceipts/0/taxYear"))
          ),
          ValueFormatError.copy(
            message = "The value must be between 0 and 99999999999.99",
            paths = Some(
              List(
                "/businessReceipts/0/grossAmount",
                "/businessReceipts/1/grossAmount",
                "/allOtherIncomeReceivedWhilstAbroad/0/amountBeforeTax",
                "/allOtherIncomeReceivedWhilstAbroad/0/taxTakenOff",
                "/allOtherIncomeReceivedWhilstAbroad/0/specialWithholdingTax",
                "/allOtherIncomeReceivedWhilstAbroad/0/taxableAmount",
                "/allOtherIncomeReceivedWhilstAbroad/0/residentialFinancialCostAmount",
                "/allOtherIncomeReceivedWhilstAbroad/0/broughtFwdResidentialFinancialCostAmount",
                "/allOtherIncomeReceivedWhilstAbroad/1/amountBeforeTax",
                "/allOtherIncomeReceivedWhilstAbroad/1/taxTakenOff",
                "/allOtherIncomeReceivedWhilstAbroad/1/specialWithholdingTax",
                "/allOtherIncomeReceivedWhilstAbroad/1/taxableAmount",
                "/allOtherIncomeReceivedWhilstAbroad/1/residentialFinancialCostAmount",
                "/allOtherIncomeReceivedWhilstAbroad/1/broughtFwdResidentialFinancialCostAmount",
                "/overseasIncomeAndGains/gainAmount",
                "/chargeableForeignBenefitsAndGifts/transactionBenefit",
                "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeSourceBenefit",
                "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeOnwardGift",
                "/chargeableForeignBenefitsAndGifts/benefitReceivedAsASettler",
                "/chargeableForeignBenefitsAndGifts/onwardGiftReceivedAsASettler",
                "/omittedForeignIncome/amount"
              ))
          ),
          RuleCountryCodeError.copy(
            paths = Some(List("/allOtherIncomeReceivedWhilstAbroad/1/countryCode"))
          ),
          RuleTaxYearRangeInvalidError.copy(
            paths = Some(List("/businessReceipts/1/taxYear"))
          )
        )

        val wrappedErrors: ErrorWrapper = errors.ErrorWrapper(
          correlationId = correlationId,
          error = BadRequestError,
          errors = Some(allInvalidValueRequestError)
        )

        val response: WSResponse = await(request().put(allInvalidValueRequestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(wrappedErrors)
      }

      "complex error scenario" in new NonTysTest {

        val createAmendErrorsRequest: JsValue = Json.parse(
          """
            |{
            |   "businessReceipts":[
            |      {
            |         "grossAmount":5000.999,
            |         "taxYear":"2019"
            |      },
            |      {
            |         "grossAmount":6000.999,
            |         "taxYear":"2019-21"
            |      }
            |   ],
            |   "allOtherIncomeReceivedWhilstAbroad":[
            |      {
            |         "countryCode":"FRANCE",
            |         "amountBeforeTax":-1999.99,
            |         "taxTakenOff":-2.23,
            |         "specialWithholdingTax":3.233,
            |         "foreignTaxCreditRelief":false,
            |         "taxableAmount":4.233,
            |         "residentialFinancialCostAmount":-2999.99,
            |         "broughtFwdResidentialFinancialCostAmount":1999.995
            |      },
            |      {
            |         "countryCode":"SBT",
            |         "amountBeforeTax":-2999.99,
            |         "taxTakenOff":-3.23,
            |         "specialWithholdingTax":4.235,
            |         "foreignTaxCreditRelief":true,
            |         "taxableAmount":5.253,
            |         "residentialFinancialCostAmount":3999.959,
            |         "broughtFwdResidentialFinancialCostAmount":-2999.99
            |      }
            |   ],
            |   "overseasIncomeAndGains":{
            |      "gainAmount":3000.993
            |   },
            |   "chargeableForeignBenefitsAndGifts":{
            |      "transactionBenefit":1999.992,
            |      "protectedForeignIncomeSourceBenefit":2999.999,
            |      "protectedForeignIncomeOnwardGift":-3999.99,
            |      "benefitReceivedAsASettler":-4999.99,
            |      "onwardGiftReceivedAsASettler":5999.996
            |   },
            |   "omittedForeignIncome":{
            |      "amount":-4000.99
            |   }
            |}
            |""".stripMargin
        )

        val createAmendErrorsResponse: JsValue = Json.parse(
          """
            {
            |    "code": "INVALID_REQUEST",
            |    "errors": [
            |        {
            |            "code": "FORMAT_COUNTRY_CODE",
            |            "message": "The provided Country code is invalid",
            |            "paths": [
            |                "/allOtherIncomeReceivedWhilstAbroad/0/countryCode"
            |            ]
            |        },
            |        {
            |            "code": "FORMAT_TAX_YEAR",
            |            "message": "The taxYear format is invalid",
            |            "paths": [
            |                "/businessReceipts/0/taxYear"
            |            ]
            |        },
            |        {
            |            "code": "FORMAT_VALUE",
            |            "message": "The value must be between 0 and 99999999999.99",
            |            "paths": [
            |                "/businessReceipts/0/grossAmount",
            |                "/businessReceipts/1/grossAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/amountBeforeTax",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/taxTakenOff",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/specialWithholdingTax",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/taxableAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/residentialFinancialCostAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/0/broughtFwdResidentialFinancialCostAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/amountBeforeTax",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/taxTakenOff",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/specialWithholdingTax",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/taxableAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/residentialFinancialCostAmount",
            |                "/allOtherIncomeReceivedWhilstAbroad/1/broughtFwdResidentialFinancialCostAmount",
            |                "/overseasIncomeAndGains/gainAmount",
            |                "/chargeableForeignBenefitsAndGifts/transactionBenefit",
            |                "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeSourceBenefit",
            |                "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeOnwardGift",
            |                "/chargeableForeignBenefitsAndGifts/benefitReceivedAsASettler",
            |                "/chargeableForeignBenefitsAndGifts/onwardGiftReceivedAsASettler",
            |                "/omittedForeignIncome/amount"
            |            ]
            |        },
            |        {
            |            "code": "RULE_COUNTRY_CODE",
            |            "message": "The country code is not a valid ISO 3166-1 alpha-3 country code",
            |            "paths": [
            |                "/allOtherIncomeReceivedWhilstAbroad/1/countryCode"
            |            ]
            |        },
            |        {
            |            "code": "RULE_TAX_YEAR_RANGE_INVALID",
            |            "message": "A tax year range of one year is required",
            |            "paths": [
            |                "/businessReceipts/1/taxYear"
            |            ]
            |        }
            |    ],
            |    "message": "Invalid request"
            |}
            |""".stripMargin
        )

        val response: WSResponse = await(request().put(createAmendErrorsRequest))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe createAmendErrorsResponse
      }
    }

    "return error according to spec" when {

      val validRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "businessReceipts": [
          |      {
          |         "grossAmount": 5000.99,
          |         "taxYear": "2018-19"
          |      },
          |      {
          |         "grossAmount": 6000.99,
          |         "taxYear": "2019-20"
          |      }
          |   ],
          |   "allOtherIncomeReceivedWhilstAbroad": [
          |      {
          |         "countryCode": "FRA",
          |         "amountBeforeTax": 1999.99,
          |         "taxTakenOff": 2.23,
          |         "specialWithholdingTax": 3.23,
          |         "foreignTaxCreditRelief": false,
          |         "taxableAmount": 4.23,
          |         "residentialFinancialCostAmount": 2999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 1999.99
          |      },
          |      {
          |         "countryCode": "IND",
          |         "amountBeforeTax": 2999.99,
          |         "taxTakenOff": 3.23,
          |         "specialWithholdingTax": 4.23,
          |         "foreignTaxCreditRelief": true,
          |         "taxableAmount": 5.23,
          |         "residentialFinancialCostAmount": 3999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 2999.99
          |      }
          |   ],
          |   "overseasIncomeAndGains": {
          |      "gainAmount": 3000.99
          |   },
          |   "chargeableForeignBenefitsAndGifts": {
          |      "transactionBenefit": 1999.99,
          |      "protectedForeignIncomeSourceBenefit": 2999.99,
          |      "protectedForeignIncomeOnwardGift": 3999.99,
          |      "benefitReceivedAsASettler": 4999.99,
          |      "onwardGiftReceivedAsASettler": 5999.99
          |   },
          |   "omittedForeignIncome": {
          |      "amount": 4000.99
          |   }
          |}
         """.stripMargin
      )

      val invalidCountryCodeRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "allOtherIncomeReceivedWhilstAbroad": [
          |      {
          |         "countryCode": "FRANCE",
          |         "amountBeforeTax": 1999.99,
          |         "taxTakenOff": 2.23,
          |         "specialWithholdingTax": 3.23,
          |         "foreignTaxCreditRelief": false,
          |         "taxableAmount": 4.23,
          |         "residentialFinancialCostAmount": 2999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 1999.99
          |      },
          |      {
          |         "countryCode": "INDIA",
          |         "amountBeforeTax": 2999.99,
          |         "taxTakenOff": 3.23,
          |         "specialWithholdingTax": 4.23,
          |         "foreignTaxCreditRelief": true,
          |         "taxableAmount": 5.23,
          |         "residentialFinancialCostAmount": 3999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 2999.99
          |      }
          |   ]
          |}""".stripMargin
      )

      val ruleCountryCodeRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "allOtherIncomeReceivedWhilstAbroad": [
          |      {
          |         "countryCode": "SBT",
          |         "amountBeforeTax": 1999.99,
          |         "taxTakenOff": 2.23,
          |         "specialWithholdingTax": 3.23,
          |         "foreignTaxCreditRelief": false,
          |         "taxableAmount": 4.23,
          |         "residentialFinancialCostAmount": 2999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 1999.99
          |      },
          |      {
          |         "countryCode": "ORK",
          |         "amountBeforeTax": 2999.99,
          |         "taxTakenOff": 3.23,
          |         "specialWithholdingTax": 4.23,
          |         "foreignTaxCreditRelief": true,
          |         "taxableAmount": 5.23,
          |         "residentialFinancialCostAmount": 3999.99,
          |         "broughtFwdResidentialFinancialCostAmount": 2999.99
          |      }
          |   ]
          |}""".stripMargin
      )

      val nonsenseRequestBody: JsValue = Json.parse(
        """
          |{
          |  "field": "value"
          |}
        """.stripMargin
      )

      val allInvalidValueRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "businessReceipts": [
          |      {
          |         "grossAmount": 5000.999,
          |         "taxYear": "2018-19"
          |      },
          |      {
          |         "grossAmount": 6000.999,
          |         "taxYear": "2019-20"
          |      }
          |   ],
          |   "allOtherIncomeReceivedWhilstAbroad": [
          |      {
          |         "countryCode": "FRA",
          |         "amountBeforeTax": -1999.99,
          |         "taxTakenOff": -2.23,
          |         "specialWithholdingTax": 3.233,
          |         "foreignTaxCreditRelief": false,
          |         "taxableAmount": 4.233,
          |         "residentialFinancialCostAmount": -2999.999,
          |         "broughtFwdResidentialFinancialCostAmount": 1999.995
          |      },
          |      {
          |         "countryCode": "IND",
          |         "amountBeforeTax": -2999.99,
          |         "taxTakenOff": -3.23,
          |         "specialWithholdingTax": 4.234,
          |         "foreignTaxCreditRelief": true,
          |         "taxableAmount": -5.237,
          |         "residentialFinancialCostAmount": -3999.992,
          |         "broughtFwdResidentialFinancialCostAmount": 2999.9956
          |      }
          |   ],
          |   "overseasIncomeAndGains": {
          |      "gainAmount": 3000.993
          |   },
          |   "chargeableForeignBenefitsAndGifts": {
          |      "transactionBenefit": 1999.998,
          |      "protectedForeignIncomeSourceBenefit": -2999.99,
          |      "protectedForeignIncomeOnwardGift": 3999.111,
          |      "benefitReceivedAsASettler": -4999.999,
          |      "onwardGiftReceivedAsASettler": 5999.995
          |   },
          |   "omittedForeignIncome": {
          |      "amount": -4000.999
          |   }
          |}
         """.stripMargin
      )

      val nonValidRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "overseasIncomeAndGains": {
          |      "gainAmount": "no"
          |   }
          |}
        """.stripMargin
      )

      val missingFieldRequestBodyJson: JsValue = Json.parse(
        """
          |{
          |   "businessReceipts": [
          |     {
          |      "grossAmount": 100.11
          |     }
          |   ]
          |}
        """.stripMargin
      )

      val countryCodeError: MtdError = CountryCodeFormatError.copy(
        paths = Some(
          Seq(
            "/allOtherIncomeReceivedWhilstAbroad/0/countryCode",
            "/allOtherIncomeReceivedWhilstAbroad/1/countryCode"
          ))
      )

      val countryCodeRuleError: MtdError = RuleCountryCodeError.copy(
        paths = Some(
          Seq(
            "/allOtherIncomeReceivedWhilstAbroad/0/countryCode",
            "/allOtherIncomeReceivedWhilstAbroad/1/countryCode"
          ))
      )

      val allInvalidValueRequestError: MtdError = ValueFormatError.copy(
        message = "The value must be between 0 and 99999999999.99",
        paths = Some(
          List(
            "/businessReceipts/0/grossAmount",
            "/businessReceipts/1/grossAmount",
            "/allOtherIncomeReceivedWhilstAbroad/0/amountBeforeTax",
            "/allOtherIncomeReceivedWhilstAbroad/0/taxTakenOff",
            "/allOtherIncomeReceivedWhilstAbroad/0/specialWithholdingTax",
            "/allOtherIncomeReceivedWhilstAbroad/0/taxableAmount",
            "/allOtherIncomeReceivedWhilstAbroad/0/residentialFinancialCostAmount",
            "/allOtherIncomeReceivedWhilstAbroad/0/broughtFwdResidentialFinancialCostAmount",
            "/allOtherIncomeReceivedWhilstAbroad/1/amountBeforeTax",
            "/allOtherIncomeReceivedWhilstAbroad/1/taxTakenOff",
            "/allOtherIncomeReceivedWhilstAbroad/1/specialWithholdingTax",
            "/allOtherIncomeReceivedWhilstAbroad/1/taxableAmount",
            "/allOtherIncomeReceivedWhilstAbroad/1/residentialFinancialCostAmount",
            "/allOtherIncomeReceivedWhilstAbroad/1/broughtFwdResidentialFinancialCostAmount",
            "/overseasIncomeAndGains/gainAmount",
            "/chargeableForeignBenefitsAndGifts/transactionBenefit",
            "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeSourceBenefit",
            "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeOnwardGift",
            "/chargeableForeignBenefitsAndGifts/benefitReceivedAsASettler",
            "/chargeableForeignBenefitsAndGifts/onwardGiftReceivedAsASettler",
            "/omittedForeignIncome/amount"
          ))
      )

      val nonValidRequestBodyErrors: MtdError = RuleIncorrectOrEmptyBodyError.copy(
        paths = Some(Seq("/overseasIncomeAndGains/gainAmount"))
      )

      val missingFieldRequestBodyErrors: MtdError = RuleIncorrectOrEmptyBodyError.copy(
        paths = Some(Seq("/businessReceipts/0/taxYear"))
      )

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError,
                                scenario: Option[String]): Unit = {
          s"validation fails with ${expectedBody.code} error ${scenario.getOrElse("")}" in new NonTysTest {
            override val nino: String       = requestNino
            override val mtdTaxYear: String = requestTaxYear

            val response: WSResponse = await(request().put(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("AA1123A", "2019-20", validRequestBodyJson, BAD_REQUEST, NinoFormatError, None),
          ("AA123456A", "20177", validRequestBodyJson, BAD_REQUEST, TaxYearFormatError, None),
          ("AA123456A", "2019-21", validRequestBodyJson, BAD_REQUEST, RuleTaxYearRangeInvalidError, None),
          ("AA123456A", "2018-19", validRequestBodyJson, BAD_REQUEST, RuleTaxYearNotSupportedError, None),
          ("AA123456A", "2019-20", invalidCountryCodeRequestBodyJson, BAD_REQUEST, countryCodeError, None),
          ("AA123456A", "2019-20", ruleCountryCodeRequestBodyJson, BAD_REQUEST, countryCodeRuleError, None),
          ("AA123456A", "2019-20", nonsenseRequestBody, BAD_REQUEST, RuleIncorrectOrEmptyBodyError, None),
          ("AA123456A", "2019-20", allInvalidValueRequestBodyJson, BAD_REQUEST, allInvalidValueRequestError, None),
          ("AA123456A", "2019-20", nonValidRequestBodyJson, BAD_REQUEST, nonValidRequestBodyErrors, Some("(invalid request body format)")),
          ("AA123456A", "2019-20", missingFieldRequestBodyJson, BAD_REQUEST, missingFieldRequestBodyErrors, Some("(missing mandatory fields)"))
        )
        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().put(requestBodyWithPCRJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        def errorBody(code: String): String =
          s"""
             |{
             |   "code": "$code",
             |   "reason": "downstream message"
             |}
            """.stripMargin

        val errors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "UNALIGNED_CESSATION_TAX_YEAR", BAD_REQUEST, RuleUnalignedCessationTaxYear)
        )

        val extraTysErrors = List(
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String          = "AA123456A"
    val correlationId: String = "X-123"

    def mtdTaxYear: String
    def downstreamUri: String

    def setupStubs(): Unit = {}

    def request(): WSRequest = {
      AuthStub.resetAll()

      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()

      buildRequest(s"/$nino/$mtdTaxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait NonTysTest extends Test {
    def mtdTaxYear: String = "2021-22"

    def downstreamUri: String = s"/income-tax/income/other/$nino/2021-22"
  }

  private trait TysIfsTest extends Test {
    def mtdTaxYear: String = "2023-24"

    def downstreamUri: String = s"/income-tax/income/other/23-24/$nino"
  }

}
