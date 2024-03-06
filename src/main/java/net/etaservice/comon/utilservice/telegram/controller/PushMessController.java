package net.etaservice.comon.utilservice.telegram.controller;

import net.etaservice.comon.utilservice.telegram.BotNotificationServiceCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/free")
public class PushMessController {

    @Autowired
    private BotNotificationServiceCommon botService;

    @PostMapping("/notify/push")
    ResponseEntity<String> pushMessageToBot(@RequestBody String string){
        botService.pushToBoss(string);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
