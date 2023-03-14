package net.etaservice.configapp.metric.service;

import net.etaservice.configapp.metric.model.MetricsApiData;
import net.etaservice.configapp.metric.repository.ApiMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApiMetricsService implements IApiMetricsService{

    @Autowired
    private ApiMetricsRepository apiMetricsRepository;

    @Override
    public long getCountMetricApiByName(String apiName) {
        return 0;
    }

    @Override
    public void saveAll(List<MetricsApiData> listMetricsApiData) {
        apiMetricsRepository.saveAll(listMetricsApiData);
    }

    @Override
    public List<MetricsApiData> findAll() {
        return apiMetricsRepository.findAll();
    }

    @Override
    public MetricsApiData findByName(String name) {
        return apiMetricsRepository.findByName(name);
    }


}
