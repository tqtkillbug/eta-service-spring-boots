package net.etaservice.configapp.metric;

import net.etaservice.configapp.metric.model.MetricsApiData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IApiMetrics {
    void increaseCount(String metricName);
    void recordTime(String metricName, long time);
    long countApiMertrics(String metricName);
    List<MetricsApiData> getMetricStorange();
    void initListMetric(List<MetricsApiData> metricsApiData);
}
