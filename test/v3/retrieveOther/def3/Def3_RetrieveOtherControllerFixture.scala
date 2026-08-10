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

package v3.retrieveOther.def3

import play.api.libs.json.{JsValue, Json}

object Def3_RetrieveOtherControllerFixture {

  val fullRetrieveOtherResponse: JsValue = Json.parse(
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

}
