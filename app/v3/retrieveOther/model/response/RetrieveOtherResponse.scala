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

package v3.retrieveOther.model.response

import api.utils.JsonWritesUtil.writesFrom
import play.api.libs.json.OWrites
import v3.retrieveOther.def1.model.response.Def1_RetrieveOtherResponse
import v3.retrieveOther.def2.model.response.Def2_RetrieveOtherResponse

trait RetrieveOtherResponse

object RetrieveOtherResponse {

  implicit val writes: OWrites[RetrieveOtherResponse] = writesFrom {
    case def1: Def1_RetrieveOtherResponse =>
      implicitly[OWrites[Def1_RetrieveOtherResponse]].writes(def1)
    case def2: Def2_RetrieveOtherResponse =>
      implicitly[OWrites[Def2_RetrieveOtherResponse]].writes(def2)
  }

}
