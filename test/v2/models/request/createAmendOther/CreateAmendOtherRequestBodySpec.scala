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

package v2.models.request.createAmendOther

import play.api.libs.json.{JsError, JsValue, Json}
import shared.utils.UnitSpec
import v2.fixtures.other.CreateAmendOtherFixtures.{requestBodyJson, requestBodyModel, requestBodyWithPCRJson}

class CreateAmendOtherRequestBodySpec extends UnitSpec {

  val emptyArraysModel: CreateAmendOtherRequestBody = CreateAmendOtherRequestBody(
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
        requestBodyJson.as[CreateAmendOtherRequestBody] shouldBe requestBodyModel.copy(postCessationReceipts = None)
      }
      "produce the expected CreateAmendOtherRequestBody object with postCessationReceipt" in {
        requestBodyWithPCRJson.as[CreateAmendOtherRequestBody] shouldBe requestBodyModel
      }
    }

    "read from JSON with all empty arrays" should {
      "return an error" in {

        emptyJson.as[CreateAmendOtherRequestBody] shouldBe emptyArraysModel
      }
    }

    "read from empty JSON" should {
      "produce an empty CreateAmendOtherRequestBody object" in {
        emptyJson.as[CreateAmendOtherRequestBody] shouldBe emptyArraysModel
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

        invalidJson.validate[CreateAmendOtherRequestBody] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(requestBodyModel) shouldBe requestBodyWithPCRJson
      }
    }
  }

}
