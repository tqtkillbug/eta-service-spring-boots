package net.etaservice.comon.utilservice.telegram.controller;

import net.etaservice.comon.utilservice.logger.LogService;
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

    @Autowired
    private LogService logService;

    @PostMapping("/notify/push")
    ResponseEntity<String> pushMessageToBot(@RequestBody String string){
        String messHanlded = logService.handleLogFromMessage(string);
        botService.pushToBoss(messHanlded);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
