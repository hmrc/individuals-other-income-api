/*
 * Copyright 2023 HM Revenue & Customs
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

package api.controllers.validators.resolvers

import api.models.errors.{MtdError, RuleTaxDeductedExceedsAmountBeforeTaxError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import v3.createAmendOther.def2.model.request.additionalIncome.AdditionalIncomeSubItem

case class ResolveTaxDeductedLessThanAmountBeforeTax() extends ResolverSupport {

  def resolver(additionalIncomeSubItem: AdditionalIncomeSubItem): Resolver[(BigDecimal, BigDecimal), Unit] =
    (amountBeforeTax, taxDeducted) =>
      if (amountBeforeTax >= taxDeducted) Valid(())
      else
        Invalid(
          List(
            RuleTaxDeductedExceedsAmountBeforeTaxError.withPaths(Seq(
              s"/additionalIncome/${additionalIncomeSubItem.getLowerCaseClassName}/amountBeforeTax",
              s"/additionalIncome/${additionalIncomeSubItem.getLowerCaseClassName}/taxDeducted"
            ))))

  def apply(additionalIncomeSubItem: AdditionalIncomeSubItem): Validated[Seq[MtdError], Option[Unit]] =
    resolver(additionalIncomeSubItem).resolveOptionally(
      Some((additionalIncomeSubItem.amountBeforeTax, additionalIncomeSubItem.taxDeducted.getOrElse(0))))

}
