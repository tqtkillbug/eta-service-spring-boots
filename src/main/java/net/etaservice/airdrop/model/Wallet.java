package net.etaservice.airdrop.model;

import lombok.Data;

@Data
public class Wallet {
    private Long id;
    private String publicKey;
    private Profile profile;
    private String privateKey;
    private String accountName;
    private String note;
    private String type;
    private String chain;
}
