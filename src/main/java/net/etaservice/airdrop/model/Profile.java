package net.etaservice.airdrop.model;

import lombok.Data;

@Data
public class Profile {
    private Long id;
    private String name;
    private String backup;
    private String location;
    private String description;
}
