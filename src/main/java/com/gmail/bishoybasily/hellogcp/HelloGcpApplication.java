package com.gmail.bishoybasily.hellogcp;

import com.google.cloud.ServiceOptions;
import com.google.cloud.errorreporting.v1beta1.ReportErrorsServiceClient;
import com.google.devtools.clouderrorreporting.v1beta1.ErrorContext;
import com.google.devtools.clouderrorreporting.v1beta1.ProjectName;
import com.google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent;
import com.google.devtools.clouderrorreporting.v1beta1.SourceLocation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@SpringBootApplication
public class HelloGcpApplication {

    private Logger logger = Logger.getLogger(getClass().getName());

    public static void main(String[] args) {
        SpringApplication.run(HelloGcpApplication.class, args);
    }

    private String template = "Hello, %s";

    @GetMapping
    public String hello(@RequestParam(required = false, value = "name") String name) {
        String respones = String.format(template, name == null ? "user" : name);

        logger.info("Logging INFO with Logback");
        logger.severe("Logging ERROR with Logback");


        String projectId = ServiceOptions.getDefaultProjectId();
        ProjectName projectName = ProjectName.of(projectId);

        // Instantiate an Error Reporting Client
        try (ReportErrorsServiceClient reportErrorsServiceClient = ReportErrorsServiceClient.create()) {

            SourceLocation myMethod = SourceLocation.newBuilder()
                    .setFilePath("Test.java")
                    .setLineNumber(10)
                    .setFunctionName("myMethod")
                    .build();

            ErrorContext errorContext = ErrorContext.newBuilder()
                    .setReportLocation(myMethod)
                    .build();

            ReportedErrorEvent customErrorEvent = ReportedErrorEvent.getDefaultInstance()
                    .toBuilder()
                    .setMessage("custom error event")
                    .setContext(errorContext)
                    .build();

            reportErrorsServiceClient.reportErrorEvent(projectName, customErrorEvent);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception encountered logging custom event", e);
        }

        return respones;
    }

}
