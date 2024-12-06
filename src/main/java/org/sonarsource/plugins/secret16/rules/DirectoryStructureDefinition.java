package org.sonarsource.plugins.secret16.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;

import static org.sonar.api.issue.impact.Severity.HIGH;
import static org.sonar.api.issue.impact.SoftwareQuality.SECURITY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.*;

public class DirectoryStructureDefinition implements RulesDefinition {
    public static final String REPOSITORY = "novelis-detection";
    public static final String JAVA_LANGUAGE = "mule";
    public static final RuleKey RULE_ON_KEY = RuleKey.of(REPOSITORY, "novelismule-rule1");

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY, JAVA_LANGUAGE).setName("My Custom Java Analyzer");


        NewRule x1Rule = repository.createRule(RULE_ON_KEY.rule())
                .setName("Flow name and file name do not match.")
                .setHtmlDescription("Flow name must match file name of the document.")
                .addDescriptionSection(descriptionSection(INTRODUCTION_SECTION_KEY, "Files must be located in <pre>/src/main/mule</pre><br /><br />", null))
                .addDescriptionSection(descriptionSection(ROOT_CAUSE_SECTION_KEY, "Please move the files in the correct structure<br /><br /><br />.", null))
                .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY, "Please set the correct structure", null))
                // optional tags
                .setTags("novelis", "mule")

                // optional status. Default value is READY.
                .setStatus(RuleStatus.BETA)

                // default severity when the rule is activated on a Quality profile. Default value is MAJOR.
                .addDefaultImpact(SECURITY, HIGH);

        x1Rule.setDebtRemediationFunction(x1Rule.debtRemediationFunctions().linearWithOffset("1h", "30min"));

        // don't forget to call done() to finalize the definition
        repository.done();
    }

    private static RuleDescriptionSection descriptionSection(String sectionKey, String htmlContent, org.sonar.api.server.rule.Context context) {
        return RuleDescriptionSection.builder()
                .sectionKey(sectionKey)
                .htmlContent(htmlContent)
                //Optional context - can be any framework or component for which you want to create detailed description
                .context(context)
                .build();
    }
}
