package net.etaservice.configapp.metric.service;

import net.etaservice.configapp.metric.model.MetricsApiData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IApiMetricsService {

    long getCountMetricApiByName(String apiName);

    void saveAll(List<MetricsApiData> listMetricsApiData);

    List<MetricsApiData> findAll();


    MetricsApiData findByName(String name);
}
