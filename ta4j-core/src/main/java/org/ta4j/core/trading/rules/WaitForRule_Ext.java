/*******************************************************************************
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2014-2017 Marc de Verdelhan, 2017-2018 Ta4j Organization 
 *   & respective authors (see AUTHORS)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.ta4j.core.trading.rules;

import org.ta4j.core.Bar;
import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

/**
 * A {@link org.ta4j.core.Rule} which waits for a number of {@link Bar} after an
 * order.
 * </p>
 * Satisfied after a fixed number of bars since the last satisfied rule.
 */
public class WaitForRule_Ext extends AbstractRule {

	private Rule rule1;
	private Rule rule2;
	private int numberOfBars;

	public WaitForRule_Ext(Rule rule1, Rule rule2, int numberOfBars) {
		this.rule1 = rule1;
		this.rule2 = rule2;
		this.numberOfBars = numberOfBars;
	}

	/**
	 * Meant to allow a bit of lag between rules
	 */
	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean firstRuleSatisfied = isInternalRuleSatisfied(rule1, index, tradingRecord);
		boolean secondRuleSatisfied = isInternalRuleSatisfied(rule2, index, tradingRecord);
		boolean satisfied = firstRuleSatisfied && secondRuleSatisfied;
		if (!satisfied && (firstRuleSatisfied || secondRuleSatisfied)) {
			if (index - numberOfBars > 0) {
				satisfied = theOtherRuleSatisfiesInPeriod(index, tradingRecord, firstRuleSatisfied);
			}
		}
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}

	private boolean theOtherRuleSatisfiesInPeriod(int index, TradingRecord tradingRecord, boolean firstRuleSatisfied) {
		boolean satisfied = false;
		for (int newIndex = index - numberOfBars; newIndex < index && !satisfied; newIndex++) {
			satisfied = isInternalRuleSatisfied(firstRuleSatisfied ? rule2 : rule1, newIndex, tradingRecord);
		}
		return satisfied;
	}

	private boolean isInternalRuleSatisfied(Rule internalRule, int index, TradingRecord tradingRecord) {
		return internalRule.isSatisfied(index, tradingRecord);
	}

}
