package net.etaservice.configapp.metric.customcontroller;

import net.etaservice.configapp.metric.ApiMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("secure/admin/metric")
public class CustomMetricController {

    @Autowired
    private ApiMetrics apiMetrics;

    @GetMapping("/list_count_api")
    public String getListMetricCountApi(){
        apiMetrics.getMetricStorange();
        return "Anh yeu Huong Nhất";
    }
}
