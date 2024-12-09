package org.sonarsource.plugins.secret16.rules;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.utils.log.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class DirectoryStructure implements Sensor {
    private static final double ARBITRARY_GAP = 2.0;
    private static final Logger LOGGER = Loggers.get(DirectoryStructure.class);

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Check the flow name and match it to the files");

        // optimisation to disable execution of sensor if project does
        // not contain Java files or if the example rule is not activated
        // in the Quality profile
        descriptor.onlyOnLanguage("mule");
        descriptor.createIssuesForRuleRepositories(DirectoryStructureDefinition.REPOSITORY);
    }

    @Override
    public void execute(SensorContext context) {
        FileSystem fs = context.fileSystem();
        Iterable<InputFile> muleFiles = fs.inputFiles(fs.predicates().hasLanguage("mule"));
        String errorMessage = "Please use the same flow name as your file name";

        for (InputFile mFile : muleFiles) {
            // no need to define the severity as it is automatically set according
            // to the configured Quality profile
            String fileName = mFile.filename();
            fileName = fileName.substring(0, fileName.length()-4);

            try {
                LineNumberReader lineReader = new LineNumberReader(new BufferedReader(new InputStreamReader(mFile.inputStream())));
                String line = "";

                while ((line = lineReader.readLine()) != null) {
                   if (line.contains("<flow")) {
                       LOGGER.info("Scanning filename: " + fileName);
                       int nameAttr = line.indexOf("name=\"")+6;
                       String value = line.substring(nameAttr,line.indexOf("\"",nameAttr));

                       if (!value.equals(fileName)) {
                           LOGGER.info("Violate flow name rule: " + value);
                           createNewIssue(errorMessage, lineReader.getLineNumber(), mFile, context);
                       }
                   }
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    public void createNewIssue(String message, Integer lineNumber, InputFile javaFile, SensorContext context) {
        NewIssue newIssue = context.newIssue()
                .forRule(DirectoryStructureDefinition.RULE_ON_KEY)

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
