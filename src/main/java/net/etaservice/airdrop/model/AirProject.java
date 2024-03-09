package net.etaservice.airdrop.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AirProject {

    private String name;
    private String description;
    private String note;
    private String sourceName;
    private String sourceLink;
    private String sourceChanelLink;

}
