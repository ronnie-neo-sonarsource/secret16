package org.sonarsource.plugins.secret16;

import org.sonar.api.Plugin;

import org.sonarsource.plugins.secret16.rules.ShortSecretDefinition;
import org.sonarsource.plugins.secret16.rules.ShortSecret;
import org.sonarsource.plugins.secret16.rules.DirectoryStructureDefinition;
import org.sonarsource.plugins.secret16.rules.DirectoryStructure;

public class CheckSecret implements Plugin {
    @Override
    public void define(Context context) {
        context.addExtensions(ShortSecretDefinition.class, ShortSecret.class);
        context.addExtensions(DirectoryStructureDefinition.class, DirectoryStructure.class);
    }
}