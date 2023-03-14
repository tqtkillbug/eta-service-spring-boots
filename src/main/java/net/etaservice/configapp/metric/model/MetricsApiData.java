package net.etaservice.configapp.metric.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "a_metrics_api_data")
public class MetricsApiData {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private long count;

}
