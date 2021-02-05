package budge.service;

import budge.model.Category;
import budge.model.ParsedEntry;
import budge.model.Rule;
import budge.model.exceptions.rules.DuplicateRuleException;
import budge.model.exceptions.rules.RuleNotFoundException;
import budge.model.exceptions.rules.RuleNotRemovedException;
import budge.repository.RulesRepository;
import budge.utils.Constants;
import budge.utils.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RulesService {

    // repo
    private final RulesRepository rulesRepository;

    /**
     * Default constructor, just initializes the repo
     */
    public RulesService() {
        rulesRepository = new RulesRepository();
    }

    /**
     * Returns the rules global from the repo
     * @return the rules global from the repo
     */
    public List<Rule> getRules() {
        return rulesRepository.getRules();
    }

    /**
     * Checks to see if a description given already exists in the rules list
     * @param description
     * @return
     */
    public boolean descriptionAlreadyExists(String description) {
        for (Rule rule : getRules()) {
            if (rule.getToReplace().equals(description)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a rule, returns null if success, returns error message for error
     * @param rule the rule to add
     * @return either null for success or an error message
     */
    public String addRule(Rule rule) {
        cleanRule(rule);
        try {
            rulesRepository.addRule(rule);
            return null;
        } catch (DuplicateRuleException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Edits a rule, returns null if success, returns error message for error
     * @param initialRule the rule to edit
     * @param newRule the rule with the new values
     * @return either null for success or an error message
     */
    public String editRule(Rule initialRule, Rule newRule) {
        try {
            rulesRepository.editRule(initialRule, newRule);
            return null;
        } catch (RuleNotFoundException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Removes a rule, returns null if success, returns error message for error
     * @param rule the rule to remove
     * @return either null for success or an error message
     */
    public String removeRule(Rule rule) {
        try {
            rulesRepository.removeRule(rule);
            return null;
        } catch (RuleNotFoundException | RuleNotRemovedException | IOException e) {
            return e.getMessage();
        }
    }

    /**
     * Filters the list of rules based on the description or category given
     * @param description optional
     * @param category optional
     * @return the result of the filtration
     */
    public List<Rule> filter(String description, String category) {
        List<Rule> filteredRules = new ArrayList<>();

        // only description
        if (StringUtils.isNotEmpty(description) && StringUtils.isEmpty(category)) {
            for (Rule rule : getRules()) {
                if (rule.getToReplace().contains(description.toUpperCase())) {
                    filteredRules.add(rule);
                }
            }
        // only category
        } else if (StringUtils.isEmpty(description) && StringUtils.isNotEmpty(category)) {
            for (Rule rule : getRules()) {
                if (rule.getCategory() == Category.fromString(category) || rule.getCategory2() == Category.fromString(category)) {
                    filteredRules.add(rule);
                }
            }
        // both description and category
        } else if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(category)) {
            for (Rule rule : getRules()) {
                if (rule.getToReplace().contains(description.toUpperCase()) &&
                        (rule.getCategory() == Category.fromString(category) || rule.getCategory2() == Category.fromString(category))) {
                    filteredRules.add(rule);
                }
            }
        // neither description nor category (no filters, just return the normal list)
        } else if (StringUtils.isEmpty(description) && StringUtils.isEmpty(category)) {
            return getRules();
        }

        return filteredRules;
    }

    /**
     * "Cleans" the rule by converting its description to uppercase
     * @param rule
     */
    private void cleanRule(Rule rule) {
        rule.setToReplace(rule.getToReplace().toUpperCase());
    }

    /**
     * Parses a rule on an entry by checking to see if a rule exists for the given entry's description
     * @param entry the entry to run the rule on
     */
    public void parseRule(ParsedEntry entry) {
        for (Rule rule : rulesRepository.getRules()) {
            if (entry.getDescription().toUpperCase().contains(rule.getToReplace())) {

                // category
                if (rule.getConditionalAmount() != null) {
                    double amount = Double.parseDouble(entry.getParsedAmount());
                    if (amount < rule.getConditionalAmount()) {
                        entry.setCategory(rule.getCategory());
                    } else {
                        entry.setCategory(rule.getCategory2());
                    }
                } else {
                    entry.setCategory(rule.getCategory());
                }

                // description
                entry.setParsedDescription(rule.getReplaceWith());
            }
        }
    }

    public ParsedEntry parseTransfer(ParsedEntry entry) {
        // appliances/From RIPLEY PATRICK XXXXXXXXXX  Share
        String description = StringUtils.EMPTY;
        String[] arr = entry.getParsedDescription().split("/");
        if (arr.length == 2) {
            if (arr[0].equals("- -SCU Mobile")) {
                description = description.concat("Mobile Transfer");
            }
        }

        return entry;
    }
}
