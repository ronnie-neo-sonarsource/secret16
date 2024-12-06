/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.plugins.secret16.rules;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Generates issues on all java files at line 1. This rule
 * must be activated in the Quality profile.
 */
public class ShortSecret implements Sensor {

    private static final double ARBITRARY_GAP = 2.0;
    private static final int LINE_1 = 1;

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Add issues on secret16 key detected in Java files");

        // optimisation to disable execution of sensor if project does
        // not contain Java files or if the example rule is not activated
        // in the Quality profile
        descriptor.onlyOnLanguage("java");
        descriptor.createIssuesForRuleRepositories(ShortSecretDefinition.REPOSITORY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        Iterable<InputFile> javaFiles = fs.inputFiles(fs.predicates().hasLanguage("java"));
        Pattern keyPattern = Pattern.compile("\\\"tsc\\-[a-zA-Z0-9-]{12}\\\"");
        Matcher matcher = keyPattern.matcher("");
        String errorMessage = "Please remove this secret16 key";

        for (InputFile javaFile : javaFiles) {
            // no need to define the severity as it is automatically set according
            // to the configured Quality profile

            try {
                LineNumberReader lineReader = new LineNumberReader(new BufferedReader(new InputStreamReader(javaFile.inputStream())));
                String line = "";
                while ((line = lineReader.readLine()) != null) {
                    matcher.reset(line);
                    if (matcher.find()) {
                        createNewIssue(errorMessage, lineReader.getLineNumber(), javaFile, context);
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    public void createNewIssue(String message, Integer lineNumber, InputFile javaFile, SensorContext context) {
        NewIssue newIssue = context.newIssue()
                .forRule(ShortSecretDefinition.RULE_ON_KEY)

                // gap is used to estimate the remediation cost to fix the debt
                .gap(ARBITRARY_GAP);
        NewIssueLocation primaryLocation = newIssue.newLocation()
                .on(javaFile)
                .at(javaFile.selectLine(lineNumber))
                .message(message);
        newIssue.at(primaryLocation);
        newIssue.save();
    }
}
