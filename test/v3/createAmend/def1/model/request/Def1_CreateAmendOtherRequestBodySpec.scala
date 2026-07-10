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

package v3.createAmend.def1.model.request

import api.utils.UnitSpec
import play.api.libs.json.{JsError, JsValue, Json}
import v3.createAmend.def1.fixtures.Def1_CreateAmendOtherFixtures.{requestBodyJson, requestBodyModel, requestBodyWithPCRJson}

class Def1_CreateAmendOtherRequestBodySpec extends UnitSpec {

  val emptyArraysModel: Def1_CreateAmendOtherRequestBody = Def1_CreateAmendOtherRequestBody(
    postCessationReceipts = Some(Seq.empty),
    businessReceipts = Some(Seq.empty),
    allOtherIncomeReceivedWhilstAbroad = Some(Seq.empty),
    overseasIncomeAndGains = None,
    chargeableForeignBenefitsAndGifts = None,
    omittedForeignIncome = None
  )

  val emptyJson: JsValue = Json.parse(
    """
      |{
      |   "postCessationReceipts": [ ],
      |   "businessReceipts": [ ],
      |   "allOtherIncomeReceivedWhilstAbroad": [ ]
      |}
    """.stripMargin
  )

  "CreateAmendOtherRequestBody" when {
    "read from valid JSON" should {
      "produce the expected CreateAmendOtherRequestBody object without postCessationReceipt" in {
        requestBodyJson.as[Def1_CreateAmendOtherRequestBody] shouldBe requestBodyModel.copy(postCessationReceipts = None)
      }
      "produce the expected CreateAmendOtherRequestBody object with postCessationReceipt" in {
        requestBodyWithPCRJson.as[Def1_CreateAmendOtherRequestBody] shouldBe requestBodyModel
      }
    }

    "read from JSON with all empty arrays" should {
      "return an error" in {

        emptyJson.as[Def1_CreateAmendOtherRequestBody] shouldBe emptyArraysModel
      }
    }

    "read from empty JSON" should {
      "produce an empty CreateAmendOtherRequestBody object" in {
        emptyJson.as[Def1_CreateAmendOtherRequestBody] shouldBe emptyArraysModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        val invalidJson = Json.parse(
          """
            |{
            |   "businessReceipts": [
            |      {
            |         "grossAmount": 5000.99,
            |         "taxYear": 2018
            |      }
            |   ]
            |}
          """.stripMargin
        )

        invalidJson.validate[Def1_CreateAmendOtherRequestBody] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(requestBodyModel) shouldBe requestBodyWithPCRJson
      }
    }
  }

}
