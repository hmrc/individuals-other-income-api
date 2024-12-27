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

package v1.controllers.validators

import cats.data.Validated
import cats.implicits.toFoldableOps
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers._
import shared.models.errors.{DateFormatError, MtdError, RuleDateRangeInvalidError}
import v1.models.request.createAmendOther._

object CreateAmendOtherRulesValidator extends RulesValidator[CreateAmendOtherRequest] with ResolverSupport {

  def validateBusinessRules(parsed: CreateAmendOtherRequest): Validated[Seq[MtdError], CreateAmendOtherRequest] = {
    import parsed.body

    combine(
      validateOptionalSeqWith(body.postCessationReceipts)(validatePostCessationReceiptsItem),
      validateOptionalSeqWith(body.businessReceipts)(validateBusinessReceipts),
      validateOptionalSeqWith(body.allOtherIncomeReceivedWhilstAbroad)(validateAllOtherIncomeReceivedWhilstAbroad),
      validateOptionalWith(body.overseasIncomeAndGains)(validateOverseasIncomeAndGains),
      validateOptionalWith(body.chargeableForeignBenefitsAndGifts)(validateChargeableForeignBenefitsAndGifts),
      validateOptionalWith(body.omittedForeignIncome)(validateOmittedForeignIncome)
    ).onSuccess(parsed)

  }

  private def validateOptionalWith[A](field: Option[A])(validator: A => Validated[Seq[MtdError], Unit]) =
    field match {
      case Some(value) => validator(value)
      case None        => valid
    }

  private def validateOptionalSeqWith[A](field: Option[Seq[A]])(validator: (A, Int) => Validated[Seq[MtdError], Unit]) =
    field match {
      case Some(values) =>
        values.zipWithIndex.toList.traverse_ { case (value, index) => validator(value, index) }
      case None => valid
    }

  private def resolveOptionalNonNegativeNumber(amount: Option[BigDecimal], path: String): Validated[Seq[MtdError], Option[BigDecimal]] =
    ResolveParsedNumber()(amount, path)

  private def resolveNonNegativeNumber(amount: BigDecimal, path: String): Validated[Seq[MtdError], BigDecimal] =
    ResolveParsedNumber()(amount, path)

  private def resolveDate(path: String) = {
    ResolveIsoDate(DateFormatError.withPath(path)).resolver
      .map(_.getYear)
      .thenValidate(inRange(1900, 2099, RuleDateRangeInvalidError.withPath(path)))
  }

  private def validateBusinessReceipts(businessReceipts: BusinessReceiptsItem, arrayIndex: Int) =
    combine(
      resolveNonNegativeNumber(
        amount = businessReceipts.grossAmount,
        path = s"/businessReceipts/$arrayIndex/grossAmount"
      ),
      ResolveTaxYear(businessReceipts.taxYear).leftMap(
        _.map(
          _.copy(paths = Some(Seq(s"/businessReceipts/$arrayIndex/taxYear")))
        )
      )
    )

  private def validatePostCessationReceiptsItem(postCessationReceiptsItem: PostCessationReceiptsItem, arrayIndex: Int) = {
    combine(
      resolveNonNegativeNumber(
        amount = postCessationReceiptsItem.amount,
        path = s"/postCessationReceipts/$arrayIndex/amount"
      ),
      ResolveTaxYear(postCessationReceiptsItem.taxYearIncomeToBeTaxed).leftMap(
        _.map(
          _.copy(paths = Some(Seq(s"/postCessationReceipts/$arrayIndex/taxYearIncomeToBeTaxed")))
        )
      ),
      resolveDate(s"/postCessationReceipts/$arrayIndex/dateBusinessCeased").resolveOptionally(postCessationReceiptsItem.dateBusinessCeased)
    )
  }

  private def validateAllOtherIncomeReceivedWhilstAbroad(allOtherIncomeReceivedWhilstAbroad: AllOtherIncomeReceivedWhilstAbroadItem,
                                                         arrayIndex: Int) =
    combine(
      ResolveParsedCountryCode(
        value = allOtherIncomeReceivedWhilstAbroad.countryCode,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/countryCode"),
      resolveOptionalNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.amountBeforeTax,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/amountBeforeTax"
      ),
      resolveOptionalNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.taxTakenOff,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/taxTakenOff"
      ),
      resolveOptionalNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.specialWithholdingTax,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/specialWithholdingTax"
      ),
      resolveNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.taxableAmount,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/taxableAmount"
      ),
      resolveOptionalNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.residentialFinancialCostAmount,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/residentialFinancialCostAmount"
      ),
      resolveOptionalNonNegativeNumber(
        amount = allOtherIncomeReceivedWhilstAbroad.broughtFwdResidentialFinancialCostAmount,
        path = s"/allOtherIncomeReceivedWhilstAbroad/$arrayIndex/broughtFwdResidentialFinancialCostAmount"
      )
    )

  private def validateOverseasIncomeAndGains(overseasIncomeAndGains: OverseasIncomeAndGains) =
    resolveNonNegativeNumber(
      amount = overseasIncomeAndGains.gainAmount,
      path = "/overseasIncomeAndGains/gainAmount"
    ).toUnit

  private def validateChargeableForeignBenefitsAndGifts(chargeableForeignBenefitsAndGifts: ChargeableForeignBenefitsAndGifts) =
    combine(
      resolveOptionalNonNegativeNumber(
        amount = chargeableForeignBenefitsAndGifts.transactionBenefit,
        path = "/chargeableForeignBenefitsAndGifts/transactionBenefit"
      ),
      resolveOptionalNonNegativeNumber(
        amount = chargeableForeignBenefitsAndGifts.protectedForeignIncomeSourceBenefit,
        path = "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeSourceBenefit"
      ),
      resolveOptionalNonNegativeNumber(
        amount = chargeableForeignBenefitsAndGifts.protectedForeignIncomeOnwardGift,
        path = "/chargeableForeignBenefitsAndGifts/protectedForeignIncomeOnwardGift"
      ),
      resolveOptionalNonNegativeNumber(
        amount = chargeableForeignBenefitsAndGifts.benefitReceivedAsASettler,
        path = "/chargeableForeignBenefitsAndGifts/benefitReceivedAsASettler"
      ),
      resolveOptionalNonNegativeNumber(
        amount = chargeableForeignBenefitsAndGifts.onwardGiftReceivedAsASettler,
        path = "/chargeableForeignBenefitsAndGifts/onwardGiftReceivedAsASettler"
      )
    )

  private def validateOmittedForeignIncome(omittedForeignIncome: OmittedForeignIncome) =
    resolveNonNegativeNumber(
      amount = omittedForeignIncome.amount,
      path = "/omittedForeignIncome/amount"
    ).toUnit

}
