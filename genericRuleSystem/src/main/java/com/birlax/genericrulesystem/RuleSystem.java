package com.birlax.genericrulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents a rule-system. A rule system can hold many rules. When evaluate on a rule system is called it will
 * return the rule-id of the rule which matches the given input.<br>
 * <br>
 * <ul>
 * <li>If multiple rules match the input criteria then first matching rule will be returned.</li>
 * <li>If no rules satisfy input conditions, constant string <b>"NO_MATCHING_RULES"</b> will be returned.</li>
 * </ul>
 * 
 * @author birlax
 */
public class RuleSystem {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Name of the rule-system. */
    private String ruleSystemName;

    /** Same as what name suggests */
    private Set<RuleCriteria> ruleCriteria;

    /** Store of rules part of this rule-system. */
    private Map<Integer, Set<Rule>> rules;

    private String ruleOutputColumnName;

    /**
     * Providing column name that is to be used to source the ranking for rules, where rules are provided as
     * {@link RuleSystem#rules } is Mandatory, Value for this field it-self in the rule can be null, in that case
     * default rank of {@link Integer.MAX_VALUE} will be used, so that these rules run last in evaluation. <br>
     * <br>
     * Negative values are reserved for the internal use, hence users are forced to provide {@code ruleRank >= 0 }<br>
     * <br>
     * Why are ruleRank <b><i>values</i></b> optional in the rule ?<br>
     * <br>
     */
    private String ruleRankColumnName;

    /**
     * @param ruleSystemName
     * @param ruleInputsAndRank
     */
    private RuleSystem() {
        super();
    }

    public RuleSystem(final String ruleSystemName, final Set<RuleCriteria> ruleCriteria,
            final String ruleOutputColumnName, final String ruleRankColumnName, final Set<Map<String, Object>> rules) {
        super();

        Objects.requireNonNull(ruleSystemName, "Rule System Name is Mandatory");
        Objects.requireNonNull(ruleCriteria, "Rule Criteria or Fields are Mandatory");
        Objects.requireNonNull(ruleOutputColumnName, "Rule Output Column is Mandatory");
        Objects.requireNonNull(ruleRankColumnName, "Rule Criteria or Fields are Mandatory");
        Objects.requireNonNull(rules, "Rules are Mandatory");
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("RuleSystem can be initialized with zero set of rules.");
        }

        this.ruleSystemName = ruleSystemName;
        // Keep the rule criterias in the sorted order.
        this.ruleCriteria = new TreeSet<>();
        this.ruleCriteria.addAll(ruleCriteria);
        this.ruleOutputColumnName = ruleOutputColumnName;
        this.ruleRankColumnName = ruleRankColumnName;
        // need to keep the rules in map in the order of their evaluation ranking.
        this.rules = new TreeMap<>();

        init(rules);
    }

    /**
     * Instantiates the rule-system with provided rules. If provided rules do not have the rule criteria for which input
     * ranks were provided all inputs will be ranked with equal weight of 1.
     * 
     * @param rules
     * @return
     */
    private boolean init(Set<Map<String, Object>> rules) {

        logger.info("Starting reloading of rules. Rules to be loaded : {} ", rules.size());

        List<RuleCriteria> rcs = new ArrayList<>(this.ruleCriteria);
        Collections.sort(rcs);
        for (Map<String, Object> rule : rules) {

            validateRuleOutputAndRuleRankColumn(rule.get(ruleRankColumnName), rule.get(ruleOutputColumnName));

            Integer ruleRank = (Integer) rule.get(ruleRankColumnName);
            ruleRank = ruleRank == null ? Integer.MAX_VALUE : ruleRank;

            String ruleId = (String) rule.get(ruleOutputColumnName);

            Map<RuleCriteria, RuleValue> ruleValues = new TreeMap<>();

            Integer finalRank = 0;
            int rcsLength = rcs.size() + 1;
            for (RuleCriteria rc : rcs) {

                rcsLength--;

                String rcName = rc.getRuleCriteriaName();
                Object rcValue = rule.get(rcName);
                if (rcValue != null) {
                    finalRank -= rc.getCriteriaRank();
                } else {
                    finalRank -= (rcsLength + rc.getCriteriaRank()) * rcs.size();
                }

                RuleValue rv = new RuleValue(rcValue, rc);
                if (rcValue == null) {
                    finalRank -= rv.returnRank();
                } else {

                    ruleValues.put(rc, rv);
                }
            }
            Rule ruleObj = new Rule(ruleId, ruleValues);

            int rank = (-1 * finalRank);
            this.rules.putIfAbsent(rank, new HashSet<>());
            this.rules.get(rank).add(ruleObj);

        }
        logger.info("Completed reloading of rules. Rules loaded : {} ", this.rules.size());
        return false;

    }

    /**
     * @param input
     * @return
     */
    public String evaluate(Map<String, Object> input) {
        for (Map.Entry<Integer, Set<Rule>> entry : rules.entrySet()) {
            for (Rule rule : entry.getValue()) {
                String result = RuleEvaluator.evaluate(input, rule);
                if (!RuleEvaluator.NO_MATCHING_RULES.equals(result)) {
                    return result;
                }
            }
        }
        return RuleEvaluator.NO_MATCHING_RULES;
    }

    private void validateRuleOutputAndRuleRankColumn(Object ruleRank, Object ruleOutput) {

        if (ruleRank != null && (!(ruleRank instanceof Integer) || ((Integer) ruleRank < 0))) {
            throw new IllegalArgumentException(
                    "Only non-negative integer values allowed for ruleRank column : " + this.ruleRankColumnName);
        }
        if (ruleOutput == null || !(ruleOutput instanceof String)) {
            throw new IllegalArgumentException(
                    "Rule output column is Mandatory and only String values allowed : " + this.ruleOutputColumnName);
        }
    }

    public String getRuleSystemName() {
        return ruleSystemName;
    }

    public void setRuleSystemName(String ruleSystemName) {
        this.ruleSystemName = ruleSystemName;
    }

    @Override
    public String toString() {
        return "RuleSystem [ruleSystemName=" + ruleSystemName + ", ruleOutputColumnName=" + ruleOutputColumnName
                + ", ruleRankColumnName=" + ruleRankColumnName + ", ruleCriteria=" + ruleCriteria + "]";
    }

}
