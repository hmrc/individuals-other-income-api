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

package v3.createAmendOther.def2.model.request.additionalIncome

import api.utils.UnitSpec
import play.api.libs.json.{JsError, Json}
import v3.createAmendOther.def2.fixtures.Def2_CreateAmendOtherFixtures.*

class MiscellaneousIncomeSpec extends UnitSpec {

  "PropertyIncomeDistributions" when {
    "read from valid JSON" should {
      "produce the expected PropertyIncomeDistributions object" in {
        additionalIncomeSubItemJson.as[MiscellaneousIncome] shouldBe miscellaneousIncomeModel
      }
    }

    "read from empty JSON" should {
      "produce a JsError" in {
        val emptyJson = Json.obj()

        emptyJson.validate[MiscellaneousIncome] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(miscellaneousIncomeModel) shouldBe additionalIncomeSubItemJson
      }
    }
  }

}
