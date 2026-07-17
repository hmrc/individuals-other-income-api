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

package v3.retrieveOther.def1.model.response

import api.models.domain.Timestamp
import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.JsonUtils
import v3.retrieveOther.model.response.RetrieveOtherResponse

case class Def1_RetrieveOtherResponse(submittedOn: Timestamp,
                                      businessReceipts: Option[Seq[BusinessReceiptsItem]],
                                      allOtherIncomeReceivedWhilstAbroad: Option[Seq[AllOtherIncomeReceivedWhilstAbroadItem]],
                                      overseasIncomeAndGains: Option[OverseasIncomeAndGains],
                                      chargeableForeignBenefitsAndGifts: Option[ChargeableForeignBenefitsAndGifts],
                                      omittedForeignIncome: Option[OmittedForeignIncome])
    extends RetrieveOtherResponse

object Def1_RetrieveOtherResponse extends JsonUtils {

  implicit val reads: Reads[Def1_RetrieveOtherResponse] = (
    (JsPath \ "submittedOn").read[Timestamp] and
      (JsPath \ "businessReceipts").readNullable[Seq[BusinessReceiptsItem]].mapEmptySeqToNone and
      (JsPath \ "allOtherIncomeReceivedWhilstAbroad").readNullable[Seq[AllOtherIncomeReceivedWhilstAbroadItem]].mapEmptySeqToNone and
      (JsPath \ "overseasIncomeAndGains").readNullable[OverseasIncomeAndGains] and
      (JsPath \ "chargeableForeignBenefitsAndGifts")
        .readNullable[ChargeableForeignBenefitsAndGifts]
        .map(_.flatMap {
          case ChargeableForeignBenefitsAndGifts.empty => None
          case chargeableForeignBenefitsAndGifts       => Some(chargeableForeignBenefitsAndGifts)
        }) and
      (JsPath \ "omittedForeignIncome").readNullable[OmittedForeignIncome]
  )(Def1_RetrieveOtherResponse.apply)

  implicit val writes: OWrites[Def1_RetrieveOtherResponse] = Json.writes[Def1_RetrieveOtherResponse]

}
