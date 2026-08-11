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

import api.models.domain.Timestamp
import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.JsonUtils
import v3.retrieveOther.model.response.RetrieveOtherResponse

case class Def3_RetrieveOtherResponse(submittedOn: Timestamp,
                                      postCessationReceipts: Option[Seq[PostCessationReceiptsItem]],
                                      allOtherIncomeReceivedWhilstAbroad: Option[Seq[AllOtherIncomeReceivedWhilstAbroadItem]],
                                      overseasIncomeAndGains: Option[OverseasIncomeAndGains],
                                      chargeableForeignBenefitsAndGifts: Option[ChargeableForeignBenefitsAndGifts],
                                      omittedForeignIncome: Option[OmittedForeignIncome],
                                      benefitFromPreOwnedAssets: Option[Seq[BenefitFromPreOwnedAssetsItem]],
                                      additionalIncome: Option[AdditionalIncome])
    extends RetrieveOtherResponse

object Def3_RetrieveOtherResponse extends JsonUtils {

  implicit val reads: Reads[Def3_RetrieveOtherResponse] = (
    (JsPath \ "submittedOn").read[Timestamp] and
      (JsPath \ "postCessationReceipts").readNullable[Seq[PostCessationReceiptsItem]].mapEmptySeqToNone and
      (JsPath \ "allOtherIncomeReceivedWhilstAbroad").readNullable[Seq[AllOtherIncomeReceivedWhilstAbroadItem]].mapEmptySeqToNone and
      (JsPath \ "overseasIncomeAndGains").readNullable[OverseasIncomeAndGains] and
      (JsPath \ "chargeableForeignBenefitsAndGifts").readNullable[ChargeableForeignBenefitsAndGifts] and
      (JsPath \ "omittedForeignIncome").readNullable[OmittedForeignIncome] and
      (JsPath \ "benefitFromPreOwnedAssets").readNullable[Seq[BenefitFromPreOwnedAssetsItem]].mapEmptySeqToNone and
      (JsPath \ "additionalIncome").readNullable[AdditionalIncome]
  )(Def3_RetrieveOtherResponse.apply)

  implicit val writes: OWrites[Def3_RetrieveOtherResponse] = Json.writes[Def3_RetrieveOtherResponse]

}
