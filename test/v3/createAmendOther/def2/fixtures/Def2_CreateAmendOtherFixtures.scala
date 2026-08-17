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

package v3.createAmendOther.def2.fixtures

import play.api.libs.json.{JsValue, Json}
import v3.createAmendOther.def2.model.request.*
import v3.createAmendOther.def2.model.request.additionalIncome.*
import v3.createAmendOther.def2.model.request.{BenefitFromPreOwnedAssets, Def2_CreateAmendOtherRequestBody}

object Def2_CreateAmendOtherFixtures {

  val postCessationReceiptsItem: PostCessationReceiptsItem = PostCessationReceiptsItem(
    customerReference = Some("String"),
    businessName = Some("Business Name"),
    dateBusinessCeased = Some("2023-05-30"),
    businessDescription = Some("Description"),
    incomeSource = Some("string"),
    amount = 99999999999.99,
    taxYearIncomeToBeTaxed = "2025-26"
  )

  val postCessationReceiptsItemJson: JsValue = Json.parse(
    s"""
       |{
       | "customerReference": "String",
       | "businessName": "Business Name",
       | "dateBusinessCeased": "2023-05-30",
       | "businessDescription": "Description",
       | "incomeSource": "string",
       | "amount": 99999999999.99,
       | "taxYearIncomeToBeTaxed": "2025-26"
       | }
       |""".stripMargin
  )

  val allOtherIncomeReceivedWhilstAbroadModel: AllOtherIncomeReceivedWhilstAbroadItem = AllOtherIncomeReceivedWhilstAbroadItem(
    countryCode = "FRA",
    amountBeforeTax = Some(1000.99),
    taxTakenOff = Some(2000.99),
    specialWithholdingTax = Some(3000.99),
    foreignTaxCreditRelief = Some(false),
    taxableAmount = 4000.99,
    residentialFinancialCostAmount = Some(5000.99),
    broughtFwdResidentialFinancialCostAmount = Some(6000.99)
  )

  val allOtherIncomeReceivedWhilstAbroadJson: JsValue = Json.parse(
    s"""
       |{
       |   "countryCode": "FRA",
       |   "amountBeforeTax": 1000.99,
       |   "taxTakenOff": 2000.99,
       |   "specialWithholdingTax": 3000.99,
       |   "foreignTaxCreditRelief": false,
       |   "taxableAmount": 4000.99,
       |   "residentialFinancialCostAmount": 5000.99,
       |   "broughtFwdResidentialFinancialCostAmount": 6000.99
       |}
       |""".stripMargin
  )

  val allOtherIncomeReceivedWhilstAbroadJsonWithoutForeignTaxCreditRelief: JsValue = Json.parse(
    s"""
       |{
       |   "countryCode": "FRA",
       |   "amountBeforeTax": 1000.99,
       |   "taxTakenOff": 2000.99,
       |   "specialWithholdingTax": 3000.99,
       |   "taxableAmount": 4000.99,
       |   "residentialFinancialCostAmount": 5000.99,
       |   "broughtFwdResidentialFinancialCostAmount": 6000.99
       |}
       |""".stripMargin
  )

  val overseasIncomeAndGainsModel: OverseasIncomeAndGains = OverseasIncomeAndGains(gainAmount = 3000.99)

  val overseasIncomeAndGainsJson: JsValue = Json.parse(
    s"""
       |{
       |   "gainAmount": 3000.99
       |}
       |""".stripMargin
  )

  val chargeableForeignBenefitsAndGiftsModel: ChargeableForeignBenefitsAndGifts = ChargeableForeignBenefitsAndGifts(
    transactionBenefit = Some(1999.99),
    protectedForeignIncomeSourceBenefit = Some(2999.99),
    protectedForeignIncomeOnwardGift = Some(3999.99),
    benefitReceivedAsASettler = Some(4999.99),
    onwardGiftReceivedAsASettler = Some(5999.99)
  )

  val chargeableForeignBenefitsAndGiftsJson: JsValue = Json.parse(
    s"""
       |{
       |   "transactionBenefit": 1999.99,
       |   "protectedForeignIncomeSourceBenefit": 2999.99,
       |   "protectedForeignIncomeOnwardGift": 3999.99,
       |   "benefitReceivedAsASettler": 4999.99,
       |   "onwardGiftReceivedAsASettler": 5999.99
       |}
       |""".stripMargin
  )

  val omittedForeignIncomeModel: OmittedForeignIncome = OmittedForeignIncome(amount = 4000.99)

  val omittedForeignIncomeJson: JsValue = Json.parse(
    s"""
       |{
       |   "amount": 4000.99
       |}
       |""".stripMargin
  )

  val benefitFromPreOwnedAssetsModel = BenefitFromPreOwnedAssets("Test", 4000.99)

  val benefitFromPreOwnedAssetsJson = Json.parse(
    s"""
       |{
       |    "typeOfAsset": "Test",
       |    "amountOfBenefit": 4000.99
       |}
       |""".stripMargin
  )

  val additionalIncomeModel = AdditionalIncome(
    Some(PropertyIncomeDistributions(4000.99, None, None, None, None)),
    Some(PersonalInsuranceBenefits(4000.99, None, None, None, None)),
    Some(IncomeFromUnauthorisedUnitTrust(4000.99, None, None, None, None)),
    Some(ProfitsFromCertificateOfDeposit(4000.99, None, None, None, None)),
    Some(NonCashBenefitsFromFormerEmployer(4000.99, None, None, None, None)),
    Some(AuthorisedPaymentsFromOverseasPensionScheme(4000.99, None, None, None, None)),
    Some(TaxableAnnualPayments(4000.99, None, None, None, None)),
    Some(MiscellaneousIncome(4000.99, None, None, None, None))
  )

  val additionalIncomeJson = Json.parse(
    """
      |{
      |   "propertyIncomeDistributions": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "personalInsuranceBenefits": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "incomeFromUnauthorisedUnitTrust": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "profitsFromCertificateOfDeposit": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "nonCashBenefitsFromFormerEmployer": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "authorisedPaymentsFromOverseasPensionScheme": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "taxableAnnualPayments": {
      |     "amountBeforeTax": 4000.99
      |   },
      |   "miscellaneousIncome": {
      |     "amountBeforeTax": 4000.99
      |   }
      |}
      |""".stripMargin
  )

  val propertyIncomeDistributionsModel = PropertyIncomeDistributions(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val personalInsuranceBenefitsModel = PersonalInsuranceBenefits(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val incomeFromUnauthorisedUnitTrustModel = IncomeFromUnauthorisedUnitTrust(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val profitsFromCertificateOfDepositModel = ProfitsFromCertificateOfDeposit(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val nonCashBenefitsFromFormerEmployerModel = NonCashBenefitsFromFormerEmployer(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val authorisedPaymentsFromOverseasPensionSchemeModel = AuthorisedPaymentsFromOverseasPensionScheme(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val taxableAnnualPaymentsModel = TaxableAnnualPayments(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val miscellaneousIncomeModel = MiscellaneousIncome(
    amountBeforeTax = 4000.99,
    allowableExpenses = None,
    taxDeducted = None,
    lossesBroughtForward = None,
    carryForwardLosses = None
  )

  val additionalIncomeSubItemJson = Json.parse(
    """
      |{
      |   "amountBeforeTax": 4000.99
      |}
      |""".stripMargin
  )

  val requestBodyModel: Def2_CreateAmendOtherRequestBody = Def2_CreateAmendOtherRequestBody(
    postCessationReceipts = Some(Seq(postCessationReceiptsItem)),
    allOtherIncomeReceivedWhilstAbroad = Some(Seq(allOtherIncomeReceivedWhilstAbroadModel)),
    overseasIncomeAndGains = Some(overseasIncomeAndGainsModel),
    chargeableForeignBenefitsAndGifts = Some(chargeableForeignBenefitsAndGiftsModel),
    omittedForeignIncome = Some(omittedForeignIncomeModel),
    benefitFromPreOwnedAssets = Some(Seq(benefitFromPreOwnedAssetsModel)),
    additionalIncome = Some(additionalIncomeModel)
  )

  val requestBodyJson: JsValue = Json.parse(
    s"""
       |{
       |   "allOtherIncomeReceivedWhilstAbroad": [$allOtherIncomeReceivedWhilstAbroadJson],
       |   "overseasIncomeAndGains": $overseasIncomeAndGainsJson,
       |   "chargeableForeignBenefitsAndGifts": $chargeableForeignBenefitsAndGiftsJson,
       |   "omittedForeignIncome": $omittedForeignIncomeJson,
       |   "benefitFromPreOwnedAssets": [$benefitFromPreOwnedAssetsJson],
       |   "additionalIncome": $additionalIncomeJson
       |}
    """.stripMargin
  )

  val requestBodyWithPCRJson: JsValue = Json.parse(
    s"""
       |{
       |  "postCessationReceipts": [$postCessationReceiptsItemJson],
       |  "allOtherIncomeReceivedWhilstAbroad": [$allOtherIncomeReceivedWhilstAbroadJson],
       |  "overseasIncomeAndGains": $overseasIncomeAndGainsJson,
       |  "chargeableForeignBenefitsAndGifts": $chargeableForeignBenefitsAndGiftsJson,
       |  "omittedForeignIncome": $omittedForeignIncomeJson,
       |  "benefitFromPreOwnedAssets": [$benefitFromPreOwnedAssetsJson],
       |  "additionalIncome": $additionalIncomeJson
       |}
    """.stripMargin
  )

  val requestBodyJsonWithoutForeignTaxCreditRelief: JsValue = Json.parse(
    s"""
       |{
       |   "allOtherIncomeReceivedWhilstAbroad": [$allOtherIncomeReceivedWhilstAbroadJsonWithoutForeignTaxCreditRelief],
       |   "overseasIncomeAndGains": $overseasIncomeAndGainsJson,
       |   "chargeableForeignBenefitsAndGifts": $chargeableForeignBenefitsAndGiftsJson,
       |   "omittedForeignIncome": $omittedForeignIncomeJson
       |}
    """.stripMargin
  )

}
