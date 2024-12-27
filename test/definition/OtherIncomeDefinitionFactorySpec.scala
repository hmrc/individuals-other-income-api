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

package definition

import cats.implicits.catsSyntaxValidatedId
import shared.config.Deprecation.NotDeprecated
import shared.config.MockSharedAppConfig
import shared.definition.APIStatus.BETA
import shared.definition.{APIDefinition, APIVersion, Definition}
import shared.routing.Version1
import shared.utils.UnitSpec

class OtherIncomeDefinitionFactorySpec extends UnitSpec {

  class Test extends MockSharedAppConfig {
    val apiDefinitionFactory = new OtherIncomeDefinitionFactory(mockSharedAppConfig)
    MockedSharedAppConfig.apiGatewayContext returns "individuals/person"
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        MockedSharedAppConfig.apiStatus(Version1) returns "BETA"
        MockedSharedAppConfig.endpointsEnabled(Version1) returns true
        MockedSharedAppConfig.deprecationFor(Version1).returns(NotDeprecated.valid).anyNumberOfTimes()

        apiDefinitionFactory.definition shouldBe
          Definition(
            api = APIDefinition(
              name = "Individuals Other Income (MTD)",
              description = "An API for providing individual other income data",
              context = "individuals/person",
              categories = List("INCOME_TAX_MTD"),
              versions = List(
                APIVersion(
                  Version1,
                  status = BETA,
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }
  }

}
