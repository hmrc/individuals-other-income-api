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

package v2.models.response.retrieveOther

import play.api.libs.json.{JsError, JsObject, Json}
import shared.utils.UnitSpec

class AllOtherIncomeReceivedWhilstAbroadItemSpec extends UnitSpec {

  private val json = Json.parse(
    """
      |{
      |   "countryCode": "FRA",
      |   "amountBeforeTax": 1999.99,
      |   "taxTakenOff": 2.23,
      |   "specialWithholdingTax": 3.23,
      |   "foreignTaxCreditRelief": false,
      |   "taxableAmount": 4.23,
      |   "residentialFinancialCostAmount": 2999.99,
      |   "broughtFwdResidentialFinancialCostAmount": 1999.99
      |}
    """.stripMargin
  )

  private val model = AllOtherIncomeReceivedWhilstAbroadItem(
    countryCode = "FRA",
    amountBeforeTax = Some(1999.99),
    taxTakenOff = Some(2.23),
    specialWithholdingTax = Some(3.23),
    foreignTaxCreditRelief = Some(false),
    taxableAmount = 4.23,
    residentialFinancialCostAmount = Some(2999.99),
    broughtFwdResidentialFinancialCostAmount = Some(1999.99)
  )

  "AllOtherIncomeReceivedWhilstAbroadItem" when {
    "read from valid JSON" should {
      "produce the expected AllOtherIncomeReceivedWhilstAbroadItem object" in {
        json.as[AllOtherIncomeReceivedWhilstAbroadItem] shouldBe model
      }
    }

    "read from empty JSON" should {
      "produce a JsError" in {
        val invalidJson = JsObject.empty

        invalidJson.validate[AllOtherIncomeReceivedWhilstAbroadItem] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(model) shouldBe json
      }
    }
  }

}
