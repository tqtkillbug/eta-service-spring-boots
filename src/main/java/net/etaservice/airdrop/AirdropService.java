package net.etaservice.airdrop;

import net.etaservice.airdrop.model.AirProject;
import net.etaservice.comon.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@PropertySource("application-${spring.profiles.active}.properties")
public class AirdropService {

    @Autowired
    private RestService restService;

    private final String TELE_DOMAIN = "https://t.me/";

    @Value("${airmon.endpoint.push}")
    private String AIRMON_PUSH_URL;


    public void handleForwardNoteAirdrop(Message channelPost){
         String groupName = channelPost.getChat().getTitle();
         if (!groupName.equals("AIRDROP_NOTE")) return;

         Chat chatForward = channelPost.getForwardFromChat();

         String chatFwTitle = chatForward.getTitle();

         String chatFwContent = channelPost.getCaption();

         String linkSourceChannel = getSourceLinkChannel(chatForward.getUserName());

         String sourceLink = getSourceLinkMessage(linkSourceChannel,channelPost.getForwardFromMessageId());
         AirProject airProject = new AirProject();
         airProject.setDescription(chatFwContent)
                 .setSourceName(chatFwTitle)
                .setSourceLink(sourceLink)
                .setSourceChanelLink(linkSourceChannel)
                .setNote("FROM NOTE_AIRDROP");

         String res = restService.callPostApi(AIRMON_PUSH_URL,airProject);
   }

   private String getSourceLinkMessage(String linkSourceChannel, long chatSourceMessId){
       return linkSourceChannel + "/" + chatSourceMessId;
   }

   private String getSourceLinkChannel(String userNameSource){
       return TELE_DOMAIN + userNameSource;
   }



}
