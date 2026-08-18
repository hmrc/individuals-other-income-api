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

package v3.createAmendOther.def2

import api.config.MockAppConfig
import api.models.domain.{Nino, TaxYear}
import api.models.errors.*
import api.models.utils.JsonErrorValidators
import api.utils.UnitSpec
import play.api.libs.json.*
import v3.createAmendOther.def2.fixtures.Def2_CreateAmendOtherFixtures.*
import v3.createAmendOther.def2.model.request.{Def2_CreateAmendOtherRequestBody, Def2_CreateAmendOtherRequestData, PostCessationReceiptsItem}

import java.time.LocalDate

class Def2_CreateAmendOtherValidatorSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private implicit val correlationId: String = "correlationId"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2025-26"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private val validRequestBodyJson: JsValue = requestBodyWithPCRJson

  def validator(nino: String, taxYear: String, body: JsValue): Def2_CreateAmendOtherValidator =
    new Def2_CreateAmendOtherValidator(nino, taxYear, body)

  private def validate(nino: String = validNino, taxYear: String = validTaxYear, body: JsValue) =
    validator(nino, taxYear, body).validateAndWrapResult()

  def singleError(error: MtdError): Left[ErrorWrapper, Nothing] = Left(ErrorWrapper(correlationId, error))

  private def expectValueFormatError(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
    def doTest(value: JsNumber): Unit = new SetupConfig {
      validate(body = body(value)) shouldBe singleError(ValueFormatError.forPathAndRange(expectedPath, "0", "99999999999.99"))
    }

    "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))
    "value is negative" in doTest(JsNumber(-0.01))
  }

  "running a validation" should {
    "return no errors" when {
      def requestWithTrailingSpaces: Def2_CreateAmendOtherRequestData = Def2_CreateAmendOtherRequestData(
        parsedNino,
        parsedTaxYear,
        Def2_CreateAmendOtherRequestBody(
          Some(
            Seq(PostCessationReceiptsItem(
              customerReference = Some("  String  "),
              businessName = Some("  Business Name  "),
              Some("2023-05-30"),
              businessDescription = Some("  Description  "),
              incomeSource = Some("  string  "),
              99999999999.99,
              "2025-26"
            ))),
          None,
          None,
          None,
          None,
          None,
          None
        )
      )

      def body(value: JsValue) = Json.parse(s"""
           |{
           |  "postCessationReceipts": [$value]
           |}""".stripMargin)

      "a valid request is supplied" in new SetupConfig {
        validate(body = validRequestBodyJson) shouldBe Right(Def2_CreateAmendOtherRequestData(parsedNino, parsedTaxYear, requestBodyModel))
      }

      "a valid request with trailing spaces is supplied" in new SetupConfig {
        validate(body = body(
          postCessationReceiptsItemJson
            .update("customerReference", JsString("  String  "))
            .update("businessName", JsString("  Business Name  "))
            .update("businessDescription", JsString("  Description  "))
            .update("incomeSource", JsString("  string  ")))) shouldBe Right(requestWithTrailingSpaces)
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new SetupConfig {
        validate("A12344A", validTaxYear, validRequestBodyJson) shouldBe singleError(NinoFormatError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in new SetupConfig {
        validate(body = JsObject.empty) shouldBe singleError(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty JSON body is submitted without any expected fields" in new SetupConfig {
        validate(body = Json.parse("""{"field": "value"}""")) shouldBe singleError(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty body with only empty arrays is submitted" in new SetupConfig {
        val emptyJson: JsValue = Json.parse(
          """
            |{
            |   "postCessationReceipts": [ ],
            |   "allOtherIncomeReceivedWhilstAbroad": [ ]
            |}
          """.stripMargin
        )
        validate(body = emptyJson) shouldBe singleError(
          RuleIncorrectOrEmptyBodyError.withPaths(List("/postCessationReceipts", "/allOtherIncomeReceivedWhilstAbroad")))
      }

      "the submitted request body is not in the correct format" in new SetupConfig {
        val invalidRequestBodyJson: JsValue = Json.parse("""
            |{
            |   "overseasIncomeAndGains": {
            |      "gainAmount": "shouldBeANumber"
            |   }
            |}""".stripMargin)

        validate(body = invalidRequestBodyJson) shouldBe singleError(RuleIncorrectOrEmptyBodyError.withPath("/overseasIncomeAndGains/gainAmount"))
      }

      "the submitted request body has missing mandatory fields" in new SetupConfig {
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

      "return TaxYearFormatError when an invalid tax year format is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("taxYearIncomeToBeTaxed", JsString("BAD_VALUE")))) shouldBe
          singleError(TaxYearFormatError.withPath("/postCessationReceipts/0/taxYearIncomeToBeTaxed"))
      }

      "return RuleTaxYearRangeInvalidError when an invalid tax year range is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("taxYearIncomeToBeTaxed", JsString("2020-22")))) shouldBe
          singleError(RuleTaxYearRangeInvalidError.withPath("/postCessationReceipts/0/taxYearIncomeToBeTaxed"))
      }

      "return RuleUnalignedCessationTaxYearError when taxYearIncomeToBeTaxed is not equal to the request tax year supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("taxYearIncomeToBeTaxed", JsString("2021-22")))) shouldBe
          singleError(RuleUnalignedCessationTaxYearError.withPath("/postCessationReceipts/0/taxYearIncomeToBeTaxed"))
      }

      "return DateFormatError when an invalid date is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("dateBusinessCeased", JsString("BAD_VALUE")))) shouldBe
          singleError(DateFormatError.withPath("/postCessationReceipts/0/dateBusinessCeased"))
      }

      "return CustomerReferenceFormatError when an invalid customer reference is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("customerReference", JsString("x" * 99)))) shouldBe
          singleError(CustomerReferenceFormatError.withPath("/postCessationReceipts/0/customerReference"))
      }

      "return BusinessNameFormatError when an invalid business name is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("businessName", JsString("*" * 106)))) shouldBe
          singleError(BusinessNameFormatError.withPath("/postCessationReceipts/0/businessName"))
      }

      "return BusinessDescriptionFormatError when an invalid business name is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("businessDescription", JsString("*" * 36)))) shouldBe
          singleError(BusinessDescriptionFormatError.withPath("/postCessationReceipts/0/businessDescription"))
      }

      "return IncomeSourceFormatError when an invalid income source is supplied" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("incomeSource", JsString("*" * 106)))) shouldBe
          singleError(IncomeSourceFormatError.withPath("/postCessationReceipts/0/incomeSource"))
      }

      "return RuleIncorrectBusinessCeasedDateError when dateBusinessCeased is equal to or after the current date" in new SetupConfig {
        validate(body = body(postCessationReceiptsItemJson.update("dateBusinessCeased", JsString(LocalDate.now.toString)))) shouldBe
          singleError(RuleIncorrectBusinessCeasedDateError.withPath("/postCessationReceipts/0/dateBusinessCeased"))
        validate(body = body(postCessationReceiptsItemJson.update("dateBusinessCeased", JsString(LocalDate.now.plusDays(1).toString)))) shouldBe
          singleError(RuleIncorrectBusinessCeasedDateError.withPath("/postCessationReceipts/0/dateBusinessCeased"))
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
        "an incorrectly formatted country code is submitted" in new SetupConfig {
          validate(body = body(allOtherIncomeReceivedWhilstAbroadJson.update("countryCode", JsString("FRANCE")))) shouldBe
            singleError(CountryCodeFormatError.withPath("/allOtherIncomeReceivedWhilstAbroad/0/countryCode"))
        }
      }

      "return CountryCodeRuleError error" when {
        "an invalid country code is submitted" in new SetupConfig {
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

    "validating benefitFromPreOwnedAssets" should {
      def body(value: JsValue) = Json.parse(s"""
           |{
           |  "benefitFromPreOwnedAssets": [
           |    $value
           |  ]
           |}""".stripMargin)

      def fromField(field: String)(value: JsNumber) = body(benefitFromPreOwnedAssetsJson.update(field, value))

      expectValueFormatError(fromField("amountOfBenefit"), "/benefitFromPreOwnedAssets/0/amountOfBenefit")

      "return RuleIncorrectOrEmptyBodyError" when {
        "an invalid type of asset is submitted" in new SetupConfig {
          validate(body = body(benefitFromPreOwnedAssetsJson.update("typeOfAsset", JsString("/////")))) shouldBe
            singleError(RuleIncorrectOrEmptyBodyError.withPath("/benefitFromPreOwnedAssets/0/typeOfAsset"))
        }
      }
    }

    "validating additionalIncome" should {
      "return RuleIncorrectOrEmptyBodyError error" when {
        "an empty JSON body is submitted" in new SetupConfig {
          validate(body = Json.parse("""{"additionalIncome": {}}""")) shouldBe
            singleError(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/additionalIncome")))
        }
      }
    }

    "validating additionalIncome sub fields" should {

      def expectSubFieldValueFormatError(field: String, basePath: String): Unit = s"return expected errors for $basePath" when {
        def doTest(value: JsNumber): Unit = new SetupConfig {
          val body: JsValue = Json.parse(s"""
               |{
               |  "additionalIncome": {
               |    "$field": {
               |      "amountBeforeTax": $value,
               |      "allowableExpenses": $value,
               |      "taxDeducted": $value,
               |      "lossesBroughtForward": $value,
               |      "carryForwardLosses": $value
               |    }
               |  }
               |}""".stripMargin)

          validate(body = body) shouldBe
            Left(
              ErrorWrapper(
                correlationId,
                ValueFormatError.withPaths(List(
                  s"$basePath/allowableExpenses",
                  s"$basePath/lossesBroughtForward",
                  s"$basePath/carryForwardLosses",
                  s"$basePath/amountBeforeTax",
                  s"$basePath/taxDeducted"
                )),
                None
              ))
        }

        "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))
        "value is negative" in doTest(JsNumber(-0.01))
      }

      expectSubFieldValueFormatError("propertyIncomeDistributions", "/additionalIncome/propertyIncomeDistributions")
      expectSubFieldValueFormatError("personalInsuranceBenefits", "/additionalIncome/personalInsuranceBenefits")
      expectSubFieldValueFormatError("incomeFromUnauthorisedUnitTrust", "/additionalIncome/incomeFromUnauthorisedUnitTrust")
      expectSubFieldValueFormatError("profitsFromCertificateOfDeposit", "/additionalIncome/profitsFromCertificateOfDeposit")
      expectSubFieldValueFormatError("nonCashBenefitsFromFormerEmployer", "/additionalIncome/nonCashBenefitsFromFormerEmployer")
      expectSubFieldValueFormatError("authorisedPaymentsFromOverseasPensionScheme", "/additionalIncome/authorisedPaymentsFromOverseasPensionScheme")
      expectSubFieldValueFormatError("taxableAnnualPayments", "/additionalIncome/taxableAnnualPayments")
      expectSubFieldValueFormatError("miscellaneousIncome", "/additionalIncome/miscellaneousIncome")

      def expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError(field: String, basePath: String): Unit =
        s"return RULE_TAX_DEDUCTED_EXCEEDS_AMOUNT_BEFORE_TAX for $basePath" when {
          def doTest(amountBeforeTax: JsNumber, taxDeducted: JsNumber): Unit = new SetupConfig {
            val body: JsValue = Json.parse(s"""
               |{
               |  "additionalIncome": {
               |    "$field": {
               |      "amountBeforeTax": $amountBeforeTax,
               |      "taxDeducted": $taxDeducted
               |    }
               |  }
               |}""".stripMargin)

            validate(body = body) shouldBe
              Left(
                ErrorWrapper(
                  correlationId,
                  RuleTaxDeductedExceedsAmountBeforeTaxError.withPaths(
                    List(
                      s"$basePath/amountBeforeTax",
                      s"$basePath/taxDeducted"
                    )),
                  None
                ))
          }
          "taxDeducted is greater than amountBeforeTax" in doTest(JsNumber(100), JsNumber(200))
        }

      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("propertyIncomeDistributions", "/additionalIncome/propertyIncomeDistributions")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("personalInsuranceBenefits", "/additionalIncome/personalInsuranceBenefits")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("incomeFromUnauthorisedUnitTrust", "/additionalIncome/incomeFromUnauthorisedUnitTrust")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("profitsFromCertificateOfDeposit", "/additionalIncome/profitsFromCertificateOfDeposit")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError(
        "nonCashBenefitsFromFormerEmployer",
        "/additionalIncome/nonCashBenefitsFromFormerEmployer")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError(
        "authorisedPaymentsFromOverseasPensionScheme",
        "/additionalIncome/authorisedPaymentsFromOverseasPensionScheme")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("taxableAnnualPayments", "/additionalIncome/taxableAnnualPayments")
      expectSubFieldRuleTaxDeductedExceedsAmountBeforeTaxError("miscellaneousIncome", "/additionalIncome/miscellaneousIncome")
    }

    "return multiple errors" when {
      "multiple fields fail validation" in new SetupConfig {

        private val multipleErrorRequestBodyJson: JsValue = Json.parse(
          """
            |{
            |   "postCessationReceipts": [
            |      {
            |         "amount": 99999999999.99,
            |         "taxYearIncomeToBeTaxed": "XXXX"
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
                TaxYearFormatError.withPaths(List(
                  "/postCessationReceipts/0/taxYearIncomeToBeTaxed"
                ))
              ))
            )
          )
      }
    }
  }

}
