package net.etaservice.comon.utilservice.logger;


import net.etaservice.comon.DateUtils;
import net.etaservice.comon.utilservice.logger.model.LogDTO;
import net.etaservice.comon.utilservice.logger.model.LogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogInfoRepository logInfoRepository;

    public LogInfo insert(LogInfo logInfo){
        return logInfoRepository.save(logInfo);
    }

    public List<LogInfo> getListByApp(String app){
       return logInfoRepository.findAllByApp(app);
    }

    public String handleLogFromMessage(String mess){
        if (!mess.contains("HOT")) return mess;
        mess = mess.replace("\"","");
        if (mess.contains("CLAIM HOT")){
            String app = "HOT";
            String[] splited = mess.split("\\s+");
            String profile = splited[2];

             String info = splited[3] + " " + splited[4] + " " + splited[5];

            LogInfo logInfo = new LogInfo();
            logInfo.setInfo(info)
                    .setApp(app)
                    .setProfile(profile);
            insert(logInfo);
        }
        return mess;
    }

    public List<LogInfo> getListLogByConditions(String app, String profile, Date dateStart){
        return logInfoRepository.findAllByAppAndProfileAndCreateDateAfter(app,profile,dateStart);
    }

    public List<LogDTO> convertToDTO(List<LogInfo> logInfos){
        List<LogDTO> logDTOS = new ArrayList<>();
        for (LogInfo logInfo : logInfos) {
            LogDTO logDTO = new LogDTO();
            logDTO.setApp(logInfo.getApp());
            logDTO.setProfile(logInfo.getProfile());
            logDTO.setInfo(logInfo.getInfo());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = formatter.format(logInfo.getCreateDate());
            logDTO.setDate(formattedDate);
            logDTOS.add(logDTO);
        }
        return logDTOS;
    }


}
