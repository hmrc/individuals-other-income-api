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

import api.utils.UnitSpec
import play.api.libs.json.{JsObject, Json}

class AdditionalIncomeSpec extends UnitSpec {

  val json: JsObject = Json.obj(
    "propertyIncomeDistributions" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "personalInsuranceBenefits" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "incomeFromUnauthorisedUnitTrust" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "profitsFromCertificateOfDeposit" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "nonCashBenefitsFromFormerEmployer" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "authorisedPaymentsFromOverseasPensionScheme" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "taxableAnnualPayments" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    ),
    "miscellaneousIncome" -> Json.obj(
      "amountBeforeTax"      -> 1999.99,
      "allowableExpenses"    -> 1999.99,
      "taxDeducted"          -> 1999.99,
      "lossesBroughtForward" -> 1999.99,
      "carryForwardLosses"   -> 1999.99
    )
  )

  val additionalIncomeItem: AdditionalIncomeItem =
    AdditionalIncomeItem(
      amountBeforeTax = 1999.99,
      allowableExpenses = Some(1999.99),
      taxDeducted = Some(1999.99),
      lossesBroughtForward = Some(1999.99),
      carryForwardLosses = Some(1999.99)
    )

  val additionalIncome: AdditionalIncome =
    AdditionalIncome(
      propertyIncomeDistributions = Some(additionalIncomeItem),
      personalInsuranceBenefits = Some(additionalIncomeItem),
      incomeFromUnauthorisedUnitTrust = Some(additionalIncomeItem),
      profitsFromCertificateOfDeposit = Some(additionalIncomeItem),
      nonCashBenefitsFromFormerEmployer = Some(additionalIncomeItem),
      authorisedPaymentsFromOverseasPensionScheme = Some(additionalIncomeItem),
      taxableAnnualPayments = Some(additionalIncomeItem),
      miscellaneousIncome = Some(additionalIncomeItem)
    )

  "AdditionalIncome" when {

    "read from JSON" should {
      "return the parsed object" in {
        json.as[AdditionalIncome] shouldBe additionalIncome
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(additionalIncome) shouldBe json
      }
    }

  }

}
