package net.etaservice.comon.utilservice.logger;

import net.etaservice.comon.DateUtils;
import net.etaservice.comon.utilservice.logger.model.LogDTO;
import net.etaservice.comon.utilservice.logger.model.LogInfo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/free/log")
public class LogController {


    @Autowired
    private LogService logService;

    @GetMapping("/list")
    public ResponseEntity<List<LogDTO>> getListLogInfo(@RequestParam String app, @RequestParam String profile){
        Date dateStart = DateUtils.getCurentDateWithoutTime();
        List<LogInfo> result = logService.getListLogByConditions(app,profile,dateStart);
        return new ResponseEntity<>(logService.convertToDTO(result), HttpStatus.OK);
    }

}
