package com.gmail.bishoybasily.hellogcp;

import com.google.api.Metric;
import com.google.api.MonitoredResource;
import com.google.cloud.ServiceOptions;
import com.google.cloud.errorreporting.v1beta1.ReportErrorsServiceClient;
import com.google.cloud.monitoring.v3.MetricServiceClient;
import com.google.devtools.clouderrorreporting.v1beta1.ErrorContext;
import com.google.devtools.clouderrorreporting.v1beta1.ProjectName;
import com.google.devtools.clouderrorreporting.v1beta1.ReportedErrorEvent;
import com.google.devtools.clouderrorreporting.v1beta1.SourceLocation;
import com.google.monitoring.v3.*;
import com.google.protobuf.util.Timestamps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private Map<String, Long> namesCounter = new HashMap<>();

    @GetMapping
    public String hello(@RequestParam(required = false, value = "name") String name) {
        String respones = String.format(template, name == null ? "user" : name);

        if (name != null) {

            namesCounter.put(name, namesCounter.getOrDefault(name, 0L) + 1L);

            logger.info("Logging INFO with Logback");
            logger.severe("Logging ERROR with Logback");

            String projectId = ServiceOptions.getDefaultProjectId();

            reportError(projectId);
            reportSpan(projectId);

        }

        return respones;
    }

    private void reportSpan(String projectId) {

        ProjectName projectName = ProjectName.of(projectId);

        try (MetricServiceClient metricServiceClient = MetricServiceClient.create()) {

            List<TimeSeries> timeSeriesList = new ArrayList<>();

            namesCounter.forEach((s, aLong) -> timeSeriesList.add(getTimeSeries(projectId, s, aLong)));

            CreateTimeSeriesRequest request = CreateTimeSeriesRequest.newBuilder()
                    .setName(projectName.toString())
                    .addAllTimeSeries(timeSeriesList)
                    .build();

            // Writes time series data
            metricServiceClient.createTimeSeries(request);

            logger.info("Done writing time series");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception dealing with stackdriver", e);
        }
    }

    private TimeSeries getTimeSeries(String projectId, String pittsburg, Long randomValue) {
        // Prepares an individual data point
        TimeInterval interval = TimeInterval.newBuilder()
                .setEndTime(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        TypedValue value = TypedValue.newBuilder()
                .setInt64Value(randomValue)
                .build();

        Point point = Point.newBuilder()
                .setInterval(interval)
                .setValue(value)
                .build();

        List<Point> pointList = new ArrayList<>();
        pointList.add(point);

        // Prepares the metric descriptor
        Map<String, String> metricLabels = new HashMap<>();

        metricLabels.put("name_id", pittsburg);
        Metric metric = Metric.newBuilder()
                .setType("custom.googleapis.com/names/counts")
                .putAllLabels(metricLabels)
                .build();

        // Prepares the monitored resource descriptor
        Map<String, String> resourceLabels = new HashMap<>();
        resourceLabels.put("project_id", projectId);
        MonitoredResource resource = MonitoredResource.newBuilder()
                .setType("global")
                .putAllLabels(resourceLabels)
                .build();

        // Prepares the time series request
        return TimeSeries.newBuilder()
                .setMetric(metric)
                .setResource(resource)
                .addAllPoints(pointList)
                .build();
    }

    private void reportError(String projectId) {
        ProjectName projectName = ProjectName.of(projectId);

        // Instantiate an Error Reporting Client
        try (ReportErrorsServiceClient reportErrorsServiceClient = ReportErrorsServiceClient.create()) {

            SourceLocation sourceLocation = SourceLocation.newBuilder()
                    .setFilePath("Test.java")
                    .setLineNumber(10)
                    .setFunctionName("sourceLocation")
                    .build();

            ErrorContext errorContext = ErrorContext.newBuilder()
                    .setReportLocation(sourceLocation)
                    .build();

            ReportedErrorEvent reportedErrorEvent = ReportedErrorEvent.getDefaultInstance()
                    .toBuilder()
                    .setMessage("custom error event")
                    .setContext(errorContext)
                    .build();

            reportErrorsServiceClient.reportErrorEvent(projectName, reportedErrorEvent);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception dealing with stackdriver", e);
        }
    }

}
