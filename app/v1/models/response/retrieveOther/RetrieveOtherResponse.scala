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

package v1.models.response.retrieveOther

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.models.domain.Timestamp
import utils.JsonUtils

case class RetrieveOtherResponse(submittedOn: Timestamp,
                                 postCessationReceipts: Option[Seq[PostCessationReceiptsItem]],
                                 businessReceipts: Option[Seq[BusinessReceiptsItem]],
                                 allOtherIncomeReceivedWhilstAbroad: Option[Seq[AllOtherIncomeReceivedWhilstAbroadItem]],
                                 overseasIncomeAndGains: Option[OverseasIncomeAndGains],
                                 chargeableForeignBenefitsAndGifts: Option[ChargeableForeignBenefitsAndGifts],
                                 omittedForeignIncome: Option[OmittedForeignIncome])

object RetrieveOtherResponse extends JsonUtils {

  implicit val reads: Reads[RetrieveOtherResponse] = (
    (JsPath \ "submittedOn").read[Timestamp] and
      (JsPath \ "postCessationReceipts").readNullable[Seq[PostCessationReceiptsItem]].mapEmptySeqToNone and
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
  )(RetrieveOtherResponse.apply _)

  implicit val writes: OWrites[RetrieveOtherResponse] = Json.writes[RetrieveOtherResponse]

}
