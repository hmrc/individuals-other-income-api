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

package v1.controllers.validators

import play.api.libs.json._
import shared.config.{MockSharedAppConfig, SharedAppConfig}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v1.fixtures.other.CreateAmendOtherFixtures._
import v1.models.request.createAmendOther.CreateAmendOtherRequest

class CreateAmendOtherValidatorSpec extends UnitSpec with JsonErrorValidators with MockSharedAppConfig {

  private implicit val correlationId: String = "correlationId"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2019-20"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private val validRequestBodyJson: JsValue = requestBodyWithPCRJson

  class Test extends MockSharedAppConfig {

    implicit val appConfig: SharedAppConfig = mockSharedAppConfig

    def validate(nino: String = validNino, taxYear: String = validTaxYear, body: JsValue): Either[ErrorWrapper, CreateAmendOtherRequest] =
      new CreateAmendOtherValidator(nino, taxYear, body).validateAndWrapResult()

    def singleError(error: MtdError): Left[ErrorWrapper, Nothing] = Left(ErrorWrapper(correlationId, error))

  }

  private def expectValueFormatError(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
    def doTest(value: JsNumber): Unit = new Test {
      validate(body = body(value)) shouldBe singleError(ValueFormatError.forPathAndRange(expectedPath, "0", "99999999999.99"))
    }

    "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))
    "value is negative" in doTest(JsNumber(-0.01))
  }

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in new Test {
        validate(body = validRequestBodyJson) shouldBe Right(CreateAmendOtherRequest(parsedNino, parsedTaxYear, requestBodyModel))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        validate("A12344A", validTaxYear, validRequestBodyJson) shouldBe singleError(NinoFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        validate(validNino, "20178", validRequestBodyJson) shouldBe singleError(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an invalid tax year is supplied" in new Test {
        validate(validNino, "2017-18", validRequestBodyJson) shouldBe singleError(RuleTaxYearNotSupportedError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in new Test {
        validate(body = JsObject.empty) shouldBe singleError(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty JSON body is submitted without any expected fields" in new Test {
        validate(body = Json.parse("""{"field": "value"}""")) shouldBe singleError(RuleIncorrectOrEmptyBodyError)
      }

      "the submitted request body is not in the correct format" in new Test {
        val invalidRequestBodyJson: JsValue = Json.parse("""
            |{
            |   "overseasIncomeAndGains": {
            |      "gainAmount": "shouldBeANumber"
            |   }
            |}""".stripMargin)

        validate(body = invalidRequestBodyJson) shouldBe singleError(RuleIncorrectOrEmptyBodyError.withPath("/overseasIncomeAndGains/gainAmount"))
      }

      "the submitted request body has missing mandatory fields" in new Test {
        validate(body = Json.parse("""{ "allOtherIncomeReceivedWhilstAbroad": [{}] }""".stripMargin)) shouldBe
          singleError(
            RuleIncorrectOrEmptyBodyError.withPaths(
              Seq(
                "/allOtherIncomeReceivedWhilstAbroad/0/countryCode",
                "/allOtherIncomeReceivedWhilstAbroad/0/taxableAmount"
              )))
      }
    }

    "validating postCessationReceipts" should {
      def body(value: JsValue) = Json.parse(s"""
          |{
          |  "postCessationReceipts": [$value]
          |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(postCessationReceiptsItemJson.update(field, value))

      expectValueFormatError(fromField("amount"), "/postCessationReceipts/0/amount")

      "return TaxYearFormatError when an invalid tax year format is supplied" in new Test {
        validate(body = body(postCessationReceiptsItemJson.update("taxYearIncomeToBeTaxed", JsString("BAD_VALUE")))) shouldBe
          singleError(TaxYearFormatError.withPath("/postCessationReceipts/0/taxYearIncomeToBeTaxed"))
      }

      "return RuleTaxYearRangeInvalidError when an invalid tax year range is supplied" in new Test {
        validate(body = body(postCessationReceiptsItemJson.update("taxYearIncomeToBeTaxed", JsString("2020-22")))) shouldBe
          singleError(RuleTaxYearRangeInvalidError.withPath("/postCessationReceipts/0/taxYearIncomeToBeTaxed"))
      }

      "return DateFormatError when an invalid date is supplied" in new Test {
        validate(body = body(postCessationReceiptsItemJson.update("dateBusinessCeased", JsString("BAD_VALUE")))) shouldBe
          singleError(DateFormatError.withPath("/postCessationReceipts/0/dateBusinessCeased"))
      }

      "return RuleDateRangeInvalidError when an out-of-range date is supplied" in new Test {
        validate(body = body(postCessationReceiptsItemJson.update("dateBusinessCeased", JsString("1899-01-01")))) shouldBe
          singleError(RuleDateRangeInvalidError.withPath("/postCessationReceipts/0/dateBusinessCeased"))
      }
    }

    "validating businessReceipts" should {
      def body(value: JsValue) = Json.parse(s"""
                                               |{
                                               |  "businessReceipts": [$value]
                                               |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(businessReceiptsJson.update(field, value))

      expectValueFormatError(fromField("grossAmount"), "/businessReceipts/0/grossAmount")

      "return TaxYearFormatError when an invalid tax year format is supplied" in new Test {
        validate(body = body(businessReceiptsJson.update("taxYear", JsString("BAD_VALUE")))) shouldBe
          singleError(TaxYearFormatError.withPath("/businessReceipts/0/taxYear"))
      }

      "return RuleTaxYearRangeInvalidError when an invalid tax year range is supplied" in new Test {
        validate(body = body(businessReceiptsJson.update("taxYear", JsString("2020-22")))) shouldBe
          singleError(RuleTaxYearRangeInvalidError.withPath("/businessReceipts/0/taxYear"))
      }
    }

    "validating allOtherIncomeReceivedWhilstAbroad" should {

      def body(value: JsValue) = Json.parse(s"""
                                               |{
                                               |  "allOtherIncomeReceivedWhilstAbroad": [$value]
                                               |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(allOtherIncomeReceivedWhilstAbroadJson.update(field, value))

      expectValueFormatError(fromField("amountBeforeTax"), "/allOtherIncomeReceivedWhilstAbroad/0/amountBeforeTax")
      expectValueFormatError(fromField("taxTakenOff"), "/allOtherIncomeReceivedWhilstAbroad/0/taxTakenOff")
      expectValueFormatError(fromField("specialWithholdingTax"), "/allOtherIncomeReceivedWhilstAbroad/0/specialWithholdingTax")
      expectValueFormatError(fromField("taxableAmount"), "/allOtherIncomeReceivedWhilstAbroad/0/taxableAmount")
      expectValueFormatError(fromField("residentialFinancialCostAmount"), "/allOtherIncomeReceivedWhilstAbroad/0/residentialFinancialCostAmount")
      expectValueFormatError(
        fromField("broughtFwdResidentialFinancialCostAmount"),
        "/allOtherIncomeReceivedWhilstAbroad/0/broughtFwdResidentialFinancialCostAmount")

      "return CountryCodeFormatError error" when {
        "an incorrectly formatted country code is submitted" in new Test {
          validate(body = body(allOtherIncomeReceivedWhilstAbroadJson.update("countryCode", JsString("FRANCE")))) shouldBe
            singleError(CountryCodeFormatError.withPath("/allOtherIncomeReceivedWhilstAbroad/0/countryCode"))
        }
      }

      "return CountryCodeRuleError error" when {
        "an invalid country code is submitted" in new Test {
          validate(body = body(allOtherIncomeReceivedWhilstAbroadJson.update("countryCode", JsString("PUR")))) shouldBe
            singleError(RuleCountryCodeError.withPath("/allOtherIncomeReceivedWhilstAbroad/0/countryCode"))
        }
      }

    }

    "validating overseasIncomeAndGains" should {

      def body(value: JsValue) = Json.parse(s"""
           |{
           |  "overseasIncomeAndGains": $value
           |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(overseasIncomeAndGainsJson.update(field, value))

      expectValueFormatError(fromField("gainAmount"), "/overseasIncomeAndGains/gainAmount")
    }

    "validating chargeableForeignBenefitsAndGifts" should {
      def body(value: JsValue) = Json.parse(s"""
           |{
           |  "chargeableForeignBenefitsAndGifts": $value
           |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(chargeableForeignBenefitsAndGiftsJson.update(field, value))

      expectValueFormatError(fromField("transactionBenefit"), "/chargeableForeignBenefitsAndGifts/transactionBenefit")
      expectValueFormatError(
        fromField("protectedForeignIncomeSourceBenefit"),
        "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeSourceBenefit")
      expectValueFormatError(fromField("protectedForeignIncomeOnwardGift"), "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeOnwardGift")
      expectValueFormatError(fromField("benefitReceivedAsASettler"), "/chargeableForeignBenefitsAndGifts/benefitReceivedAsASettler")
      expectValueFormatError(fromField("onwardGiftReceivedAsASettler"), "/chargeableForeignBenefitsAndGifts/onwardGiftReceivedAsASettler")
    }

    "validating omittedForeignIncome" should {
      def body(value: JsValue) = Json.parse(s"""
           |{
           |  "omittedForeignIncome": $value
           |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(omittedForeignIncomeJson.update(field, value))

      expectValueFormatError(fromField("amount"), "/omittedForeignIncome/amount")
    }

    "return multiple errors" when {
      "multiple fields fail validation" in new Test {

        private val multipleErrorRequestBodyJson: JsValue = Json.parse(
          """
            |{
            |   "postCessationReceipts": [
            |      {
            |         "amount": 99999999999.99,
            |         "taxYearIncomeToBeTaxed": "XXXX"
            |      }
            |   ],
            |   "businessReceipts": [
            |      {
            |         "grossAmount": 5000.99,
            |         "taxYear": "XXXX"
            |      },
            |      {
            |         "grossAmount": 6000.99,
            |         "taxYear": "YYYY"
            |      }
            |   ],
            |   "allOtherIncomeReceivedWhilstAbroad": [
            |      {
            |         "countryCode": "FRANCE",
            |         "taxableAmount": 4.23
            |      }
            |   ]
            |}
    """.stripMargin
        )

        validate(body = multipleErrorRequestBodyJson) shouldBe
          Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(Seq(
                CountryCodeFormatError.withPath("/allOtherIncomeReceivedWhilstAbroad/0/countryCode"),
                TaxYearFormatError.withPaths(
                  List(
                    "/postCessationReceipts/0/taxYearIncomeToBeTaxed",
                    "/businessReceipts/0/taxYear",
                    "/businessReceipts/1/taxYear"
                  ))
              ))
            ))
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors (path parameters)" in new Test {
        validate("A12344A", "20178", validRequestBodyJson) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError))))
      }
    }
  }

}
