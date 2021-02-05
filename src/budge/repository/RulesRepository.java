package budge.repository;

import budge.model.Rule;
import budge.model.exceptions.rules.DuplicateRuleException;
import budge.model.exceptions.rules.RuleNotFoundException;
import budge.model.exceptions.rules.RuleNotRemovedException;
import budge.utils.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RulesRepository {

    // globals
    List<Rule> rules;
    File file = new File("src/resources/repository/rules.csv");

    /**
     * Default constructor, initializes the rules global and reads in all the rules
     */
    public RulesRepository() {
        rules = new ArrayList<>();
        readInRules();
    }

    /**
     * Returns the rules global
     * @return the rules global
     */
    public List<Rule> getRules() {
        return this.rules;
    }

    /**
     * Reads in the rules from the file and stores them in the rules global
     */
    private void readInRules() {
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("###") || line.trim().isEmpty()) {

                } else {
                    rules.add(new Rule(line));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(file.getName() + " wasn't found!");
        } catch (IOException e) {
            System.err.println("IOException when opening " + file.getName() + " to read!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException | NullPointerException e) {
                System.err.println("Couldn't close the reader stream for some reason!");
            }
        }
    }

    /**
     * Writes the rules global to the file
     * @throws IOException
     */
    private void writeOutRules() throws IOException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Rule rule : this.rules) {
                writer.write(rule.toString());
                writer.write(Constants.NEWLINE);
            }
            writer.close();
        } catch (IOException e) {
            throw new IOException("IOException while trying to write out to rules file!  " + e.getMessage());
        }
    }

    /**
     * Adds a new rule to the rules list and at the end of the file
     * @param rule the rule to add
     * @throws DuplicateRuleException
     * @throws IOException
     */
    public void addRule(Rule rule) throws DuplicateRuleException, IOException {
        // check if it already exists
        for (Rule ruleInList : this.rules) {
            if (rule.equals(ruleInList)) {
                throw new DuplicateRuleException("Rule already exists!");
            }
        }

        // add it to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.append(rule.toString());
            writer.append(Constants.NEWLINE);
            rules.add(rule);
        } catch (IOException e) {
            throw new IOException("IOException while trying to read " + file.getName() + "!  " + e.getMessage());
        }
    }

    /**
     * Edits a rule in the rules list
     * @param initialRule the rule to edit
     * @param newRule the rule with the new values
     * @throws RuleNotFoundException
     * @throws IOException
     */
    public void editRule(Rule initialRule, Rule newRule) throws RuleNotFoundException, IOException {
        String toReplace = initialRule.toString();
        String replaceWith = newRule.toString();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder inputBuffer = new StringBuilder();
            String line;

            // store the contents of the file in the input buffer
            while ((line = reader.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }
            reader.close();

            // do the replace
            String fileContents = inputBuffer.toString();
            String newFileContents = fileContents.replace(toReplace, replaceWith);

            // check to see if that rule was even found
            if (fileContents.equals(newFileContents)) {
                throw new RuleNotFoundException("Couldn't find rule with description: " + initialRule.getToReplace());
            }

            // output the new contents to the file
            FileWriter writer = new FileWriter(file);
            writer.append(newFileContents);
            writer.close();

            // set the old rule to the new rule in the rules list
            for (int i = 0; i < rules.size(); i++) {
                if (rules.get(i).equals(initialRule)) {
                    rules.set(i, newRule);
                }
            }

        } catch (IOException e) {
            throw new IOException("IOException while editing a rule!  " + e.getMessage());
        }
    }

    /**
     * Removes a rule (in a kinda hacky way)
     * @param rule the rule to remove
     * @throws RuleNotRemovedException
     * @throws RuleNotFoundException
     * @throws IOException
     */
    public void removeRule(Rule rule) throws RuleNotRemovedException, RuleNotFoundException, IOException {
        // remove the rule
        if (!rules.remove(rule)) {
            throw new RuleNotFoundException("Rule with description " + rule.getToReplace() + " was not found!");
        }

        // output the new rules array to the file
        try {
            writeOutRules();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        // verify it was deleted
        if (rules.contains(rule)) {
            throw new RuleNotRemovedException("Rule with description " + rule.getToReplace() + " was not removed!");
        }
    }
}
