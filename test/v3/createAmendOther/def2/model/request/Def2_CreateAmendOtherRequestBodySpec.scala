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

package v3.createAmendOther.def2.model.request

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsValue, Json}
import v3.createAmendOther.def2.fixtures.Def2_CreateAmendOtherFixtures.{requestBodyJson, requestBodyModel, requestBodyWithPCRJson}

class Def2_CreateAmendOtherRequestBodySpec extends UnitSpec {

  val emptyArraysModel: Def2_CreateAmendOtherRequestBody = Def2_CreateAmendOtherRequestBody(
    postCessationReceipts = Some(Seq.empty),
    allOtherIncomeReceivedWhilstAbroad = Some(Seq.empty),
    overseasIncomeAndGains = None,
    chargeableForeignBenefitsAndGifts = None,
    omittedForeignIncome = None,
    benefitFromPreOwnedAssets = None,
    additionalIncome = None
  )

  val emptyJson: JsValue = Json.parse(
    """
      |{
      |   "postCessationReceipts": [ ],
      |   "allOtherIncomeReceivedWhilstAbroad": [ ]
      |}
    """.stripMargin
  )

  "CreateAmendOtherRequestBody" when {
    "read from valid JSON" should {
      "produce the expected CreateAmendOtherRequestBody object without postCessationReceipt" in {
        requestBodyJson.as[Def2_CreateAmendOtherRequestBody] shouldBe requestBodyModel.copy(postCessationReceipts = None)
      }
      "produce the expected CreateAmendOtherRequestBody object with postCessationReceipt" in {
        requestBodyWithPCRJson.as[Def2_CreateAmendOtherRequestBody] shouldBe requestBodyModel
      }
    }

    "read from JSON with all empty arrays" should {
      "return an error" in {

        emptyJson.as[Def2_CreateAmendOtherRequestBody] shouldBe emptyArraysModel
      }
    }

    "read from empty JSON" should {
      "produce an empty CreateAmendOtherRequestBody object" in {
        emptyJson.as[Def2_CreateAmendOtherRequestBody] shouldBe emptyArraysModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        val invalidJson = Json.parse(
          """
            |{
            |  "additionalIncome": {
            |    "propertyIncomeDistributions": {
            |      "amountBeforeTax": 4000.99,
            |      "allowableExpenses": true
            |    }
            |  }
            |}
          """.stripMargin
        )

        val res = invalidJson.validate[Def2_CreateAmendOtherRequestBody]
        println(res)
        res shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(requestBodyModel) shouldBe requestBodyWithPCRJson
      }
    }
  }

}
