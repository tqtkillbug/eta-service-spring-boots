package net.etaservice.configapp.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import net.etaservice.configapp.metric.model.MetricsApiData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ApiMetrics implements  IApiMetrics {

    @Autowired
    private MeterRegistry meterRegistry;


    @Override
    public void increaseCount(String apiName) {
        Counter.builder("api."+apiName.replace("/","_")+".count").register(meterRegistry).increment();
    }

    @Override
    public void recordTime(String apiName, long time) {
        Timer.builder("api."+apiName+".time").register(meterRegistry).record(time, TimeUnit.MILLISECONDS);
   }

    @Override
    public long countApiMertrics(String metricName) {
        Counter counter = meterRegistry.counter("api."+metricName.replace("/","_")+".count");
        return (long) counter.count();
    }

    @Override
    public List<MetricsApiData> getMetricStorange(){
       List<Meter> meters = meterRegistry.getMeters();
        List<MetricsApiData> metricsApiData = new ArrayList<>();
       for (Meter meter : meters) {
           String name = meter.getId().getName();
           if (name.startsWith("api.") && name.endsWith(".count")) {
               long count = (long) ((Counter) meter).count();
               MetricsApiData metricsApi = new MetricsApiData();
               metricsApi.setName(name);
               metricsApi.setCount(count);
               metricsApiData.add(metricsApi);
           }
       }
       return metricsApiData;
   }

    @Override
    public void initListMetric(List<MetricsApiData> metricsApiData) {
        for (MetricsApiData  me: metricsApiData) {
            Counter counter;
            counter = meterRegistry.counter(me.getName());
            counter.increment(me.getCount());
        }
    }


}
