package net.etaservice.comon.utilservice.telegram.customanotation;

import lombok.extern.slf4j.Slf4j;
import net.etaservice.comon.utilservice.telegram.BotController;
import net.etaservice.comon.utilservice.telegram.BotNotification;
import net.etaservice.comon.utilservice.telegram.BotNotificationServiceCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AnnotationHandler {

    @Autowired
    private BotNotificationServiceCommon notificationServiceCommon;

    @Autowired
    ApplicationContext applicationContext;

    public void callMethodByAnoBotCallBack(String name, Long chatId, Update update) throws Exception {
        List<Method> methods = getListMethodBotRoute();
        for (Method method : methods) {
            try {
                if (method.isAnnotationPresent(BotCallBack.class)) {
                BotCallBack botCallBack = method.getAnnotation(BotCallBack.class);
                if (name.equals(botCallBack.name())) {
                        Object beanInstance = applicationContext.getBean(method.getDeclaringClass());
                        method.invoke(beanInstance,notificationServiceCommon,chatId,update);
                        return;
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Method not found");
            }
        }
    }

    public List<Method> getListMethodBotRoute(){
        List<Method> annotatedMethods = new ArrayList<>();
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Class<?> clazz = applicationContext.getType(beanName);
            if (clazz.isAnnotationPresent(BotRoute.class)) {
                Method[] methods = clazz.getDeclaredMethods();
                annotatedMethods.addAll(Arrays.asList(methods));
            }
        }
        return annotatedMethods;
    }

}
