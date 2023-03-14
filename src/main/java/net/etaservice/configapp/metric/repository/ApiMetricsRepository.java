package net.etaservice.configapp.metric.repository;

import net.etaservice.configapp.metric.model.MetricsApiData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiMetricsRepository extends JpaRepository<MetricsApiData, Long> {

   MetricsApiData findByName(String name);

}
