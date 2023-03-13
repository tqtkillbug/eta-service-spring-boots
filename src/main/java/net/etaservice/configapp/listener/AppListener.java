package net.etaservice.configapp.listener;

import net.etaservice.configapp.metric.IApiMetrics;
import net.etaservice.configapp.metric.model.MetricsApiData;
import net.etaservice.configapp.metric.service.IApiMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AppListener {

    @Autowired
    private IApiMetrics apiMetrics;

    @Autowired
    private IApiMetricsService apiMetricsService;

    @EventListener
    public void onAppRestartedEvent(ContextRefreshedEvent event) {
        // xử lý logic khi ứng dụng được restart
//        saveMetricDataToDb();
    }

    @EventListener
    public void onAppShutdownEvent(ContextClosedEvent event) {
        // xử lý logic khi ứng dụng bị tắt
        saveMetricDataToDb();
    }

    @PostConstruct
    public void init() {
        List<MetricsApiData> metricsApiData =  apiMetricsService.findAll();
        if (!metricsApiData.isEmpty()){
            apiMetrics.initListMetric(metricsApiData);
        }
    }

    private void saveMetricDataToDb(){
        List<MetricsApiData> metricsApiData = apiMetrics.getMetricStorange();
       for (MetricsApiData me : metricsApiData){
          MetricsApiData existing = apiMetricsService.findByName(me.getName());
          if (existing != null){
              me.setId(existing.getId());
          }
        }
        apiMetricsService.saveAll(metricsApiData);
    }
}