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

package v3.createAmendOther

import api.controllers.RequestContext
import api.services.ServiceOutcome
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import v3.createAmendOther.def1.model.request.Def1_CreateAmendOtherRequestData

import scala.concurrent.{ExecutionContext, Future}

trait MockCreateAmendOtherService extends TestSuite with MockFactory {

  val mockCreateAmendOtherService: CreateAmendOtherService = mock[CreateAmendOtherService]

  object MockCreateAmendOtherService {

    def createAmend(requestData: Def1_CreateAmendOtherRequestData): CallHandler[Future[ServiceOutcome[Unit]]] = {
      (mockCreateAmendOtherService
        .createAmend(_: Def1_CreateAmendOtherRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)
    }

  }

}
