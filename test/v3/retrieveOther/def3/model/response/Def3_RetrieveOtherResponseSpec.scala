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

package v3.retrieveOther.def3.model.response

import api.models.domain.Timestamp
import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsObject, Json}

class Def3_RetrieveOtherResponseSpec extends UnitSpec {

  private val json = Json.parse(
    """
      |{
      |   "submittedOn":"2026-04-04T01:01:01.000Z",
      |   "postCessationReceipts":[
      |      {
      |         "customerReference":"String",
      |         "businessName":"LsMBEqEWnG9j,9JP9RpgkGmIcF2I30.NpxZRtgN3zA7-b8h-LvHvApdJtpY",
      |         "dateBusinessCeased":"2023-06-01",
      |         "businessDescription":"u2e'VarLXLa\\W&RHojlOZIqm9NDG",
      |         "incomeSource":"string",
      |         "amount":99999999999.99,
      |         "taxYearIncomeToBeTaxed":"2019-20"
      |      }
      |   ],
      |   "allOtherIncomeReceivedWhilstAbroad":[
      |      {
      |         "countryCode":"FRA",
      |         "amountBeforeTax":1999.99,
      |         "taxTakenOff":2.23,
      |         "specialWithholdingTax":3.23,
      |         "taxableAmount":4.23,
      |         "residentialFinancialCostAmount":2999.99,
      |         "broughtFwdResidentialFinancialCostAmount":1999.99
      |      },
      |      {
      |         "countryCode":"IND",
      |         "amountBeforeTax":2999.99,
      |         "taxTakenOff":3.23,
      |         "specialWithholdingTax":4.23,
      |         "foreignTaxCreditRelief":true,
      |         "taxableAmount":5.23,
      |         "residentialFinancialCostAmount":3999.99,
      |         "broughtFwdResidentialFinancialCostAmount":2999.99
      |      }
      |   ],
      |   "overseasIncomeAndGains":{
      |      "gainAmount":3000.99
      |   },
      |   "chargeableForeignBenefitsAndGifts":{
      |      "transactionBenefit":1999.99,
      |      "protectedForeignIncomeSourceBenefit":2999.99,
      |      "protectedForeignIncomeOnwardGift":3999.99,
      |      "benefitReceivedAsASettler":4999.99,
      |      "onwardGiftReceivedAsASettler":5999.99
      |   },
      |   "omittedForeignIncome":{
      |      "amount":4000.99
      |   },
      |   "benefitFromPreOwnedAssets": [
      |    {
      |      "typeOfAsset": "Residential property",
      |      "amountOfBenefit": 1999.99
      |    }
      |  ],
      |  "additionalIncome": {
      |    "propertyIncomeDistributions": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "personalInsuranceBenefits": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "incomeFromUnauthorisedUnitTrust": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "profitsFromCertificateOfDeposit": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "nonCashBenefitsFromFormerEmployer": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "authorisedPaymentsFromOverseasPensionScheme": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "taxableAnnualPayments": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    },
      |    "miscellaneousIncome": {
      |      "amountBeforeTax": 1999.99,
      |      "allowableExpenses": 1999.99,
      |      "taxDeducted": 1999.99,
      |      "lossesBroughtForward": 1999.99,
      |      "carryForwardLosses": 1999.99
      |    }
      |  }
      |}
    """.stripMargin
  )

  private val postCessationReceiptsItemModel = Seq(
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

  private val allOtherIncomeReceivedWhilstAbroadItemModel = Seq(
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

  private val benefitFromPreOwnedAssetsModel = Seq(BenefitFromPreOwnedAssetsItem(typeOfAsset = "Residential property", amountOfBenefit = 1999.99))

  private val additionalIncomeItem = AdditionalIncomeItem(
    amountBeforeTax = 1999.99,
    allowableExpenses = Some(1999.99),
    taxDeducted = Some(1999.99),
    lossesBroughtForward = Some(1999.99),
    carryForwardLosses = Some(1999.99)
  )

  private val additionalIncome = AdditionalIncome(
    propertyIncomeDistributions = Some(additionalIncomeItem),
    personalInsuranceBenefits = Some(additionalIncomeItem),
    incomeFromUnauthorisedUnitTrust = Some(additionalIncomeItem),
    profitsFromCertificateOfDeposit = Some(additionalIncomeItem),
    nonCashBenefitsFromFormerEmployer = Some(additionalIncomeItem),
    authorisedPaymentsFromOverseasPensionScheme = Some(additionalIncomeItem),
    taxableAnnualPayments = Some(additionalIncomeItem),
    miscellaneousIncome = Some(additionalIncomeItem)
  )

  private val responseModel = Def3_RetrieveOtherResponse(
    submittedOn = Timestamp("2026-04-04T01:01:01.000Z"),
    Some(postCessationReceiptsItemModel),
    Some(allOtherIncomeReceivedWhilstAbroadItemModel),
    Some(overseasIncomeAndGainsModel),
    Some(chargeableForeignBenefitsAndGiftsModel),
    Some(omittedForeignIncomeModel),
    Some(benefitFromPreOwnedAssetsModel),
    Some(additionalIncome)
  )

  "Def3_RetrieveOtherResponse" when {
    "read from valid JSON" should {
      "produce the expected Def3_RetrieveOtherResponse object" in {
        json.as[Def3_RetrieveOtherResponse] shouldBe responseModel
      }
    }

    "read from json with empty chargeableForeignBenefitsAndGifts object, benefitFromPreOwnedAssets and allOtherIncomeReceivedWhilstAbroad arrays" should {
      "produce a JsError" in {
        val invalidJson = Json.parse(
          """
            |{
            |   "allOtherIncomeReceivedWhilstAbroad": [ ],
            |   "chargeableForeignBenefitsAndGifts": { },
            |   "benefitFromPreOwnedAssets": [ ]
            |}
          """.stripMargin
        )

        invalidJson.validate[Def3_RetrieveOtherResponse] shouldBe a[JsError]

      }
    }

    "read from empty JSON" should {
      "produce a JsError" in {
        val emptyJson = JsObject.empty

        emptyJson.validate[Def3_RetrieveOtherResponse] shouldBe a[JsError]
      }
    }

    "read from a valid JSON with submittedOn field," should {
      "produce a expected Def3_RetrieveOtherResponse object" in {
        val json = Json.parse(
          """
            |{
            |   "submittedOn":"2026-04-04T01:01:01Z"
            |}
          """.stripMargin
        )

        json.as[Def3_RetrieveOtherResponse] shouldBe
          Def3_RetrieveOtherResponse(submittedOn = Timestamp("2026-04-04T01:01:01.000Z"), None, None, None, None, None, None, None)
      }
    }

    "read from a valid JSON with missing foreignTaxCreditRelief field in allOtherIncomeReceivedWhilstAbroad" should {
      "produce an expected Def3_RetrieveOtherResponse object with foreignTaxCreditRelief as false" in {
        val json = Json.parse(
          """
            |{
            |   "submittedOn": "2026-04-04T01:01:01Z",
            |   "allOtherIncomeReceivedWhilstAbroad": [
            |      {
            |         "countryCode": "FRA",
            |         "taxableAmount": 4.23
            |      }
            |   ]
            |}
          """.stripMargin
        )

        json.as[Def3_RetrieveOtherResponse] shouldBe
          Def3_RetrieveOtherResponse(
            submittedOn = Timestamp("2026-04-04T01:01:01.000Z"),
            None,
            Some(Seq(AllOtherIncomeReceivedWhilstAbroadItem("FRA", None, None, None, None, 4.23, None, None))),
            None,
            None,
            None,
            None,
            None
          )

      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(responseModel) shouldBe json
      }
    }
  }

}
