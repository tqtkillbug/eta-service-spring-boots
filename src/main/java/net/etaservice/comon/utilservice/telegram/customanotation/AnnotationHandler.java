package net.etaservice.comon.utilservice.telegram.customanotation;

import lombok.extern.slf4j.Slf4j;
import net.etaservice.comon.utilservice.telegram.BotController;
import net.etaservice.comon.utilservice.telegram.BotNotification;
import net.etaservice.comon.utilservice.telegram.BotNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

@Service
@Slf4j
public class AnnotationHandler {

    @Autowired
    private BotNotificationService botNotificationService;

    public void callMethodByAnoBotCallBack(String name, Long chatId) throws Exception {
        Method[] methods = BotController.class.getDeclaredMethods();
        for (Method method : methods) {
            BotCallBack annotation = method.getAnnotation(BotCallBack.class);
            if (annotation == null) continue;
            if (annotation.name().equals(name)) {
                method.invoke(new BotController(),botNotificationService,chatId);
                return;
            }
        }
        log.error("Method not found");
    }

}
