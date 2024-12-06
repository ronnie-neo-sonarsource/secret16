package org.sonarsource.plugins.secret16.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;

import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.INTRODUCTION_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;
import static org.sonar.api.issue.impact.SoftwareQuality.SECURITY;
import static org.sonar.api.issue.impact.Severity.HIGH;

public class ShortSecretDefinition implements RulesDefinition {
    public static final String REPOSITORY = "secret-detection";
    public static final String JAVA_LANGUAGE = "java";
    public static final RuleKey RULE_ON_KEY = RuleKey.of(REPOSITORY, "sonarsvc-key");

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY, JAVA_LANGUAGE).setName("My Custom Java Analyzer");


        NewRule x1Rule = repository.createRule(RULE_ON_KEY.rule())
                .setName("Secret16 key should not be disclosed.")
                .setHtmlDescription("A secret key length of 16 or less has been found")
                .addDescriptionSection(descriptionSection(INTRODUCTION_SECTION_KEY, "Please remove this key<br /><br /><br />", null))
                .addDescriptionSection(descriptionSection(ROOT_CAUSE_SECTION_KEY, "The root cause of this issue is due to the key<br /><br /><br />.", null))
                .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY, "Please remove this key", null))
                // optional tags
                .setTags("security", "secrets")

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
