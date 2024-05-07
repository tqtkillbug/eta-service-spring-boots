package net.etaservice.airdrop;

import net.etaservice.airdrop.model.AirProject;
import net.etaservice.airdrop.model.Wallet;
import net.etaservice.comon.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

@Service
@PropertySource("application-${spring.profiles.active}.properties")
public class AirdropService {

    @Autowired
    private RestService restService;

    private final String TELE_DOMAIN = "https://t.me/";

    @Value("${airmon.host}")
    private String AIRMON_HOST;

    @Value("${airmon.uri.push}")
    private String PUSH_URI;


    @Value("${airmon.uri.getwallet}")
    private String GET_WALLET_URI;

    public Wallet getWalletInfoFromAirmon(Long id){
        String fullUrl = AIRMON_HOST + GET_WALLET_URI + "/" + id;
        return restService.callGetApi(fullUrl,Wallet.class);
    }


    public void handleForwardNoteAirdrop(Message channelPost){
         String groupName = channelPost.getChat().getTitle();
         if (!groupName.equals("AIRDROP_NOTE")) return;

         Chat chatForward = channelPost.getForwardFromChat();

         String chatFwTitle = chatForward.getTitle();

         String chatFwContent = channelPost.getCaption();

        List<MessageEntity> listEntity = channelPost.getCaptionEntities();

        chatFwContent =  handleDescriptionBindLink(chatFwContent,listEntity);

         String linkSourceChannel = getSourceLinkChannel(chatForward.getUserName());

         String sourceLink = getSourceLinkMessage(linkSourceChannel,channelPost.getForwardFromMessageId());
         AirProject airProject = new AirProject();
         airProject.setDescription(chatFwContent)
                 .setSourceName(chatFwTitle)
                .setSourceLink(sourceLink)
                .setSourceChanelLink(linkSourceChannel)
                .setNote("NOTE_AIRDROP");
         String url = AIRMON_HOST + PUSH_URI;
         String res = restService.callPostApi(url,airProject);
   }

    private String handleDescriptionBindLink(String chatFwContent, List<MessageEntity> listEntity) {
        // Sắp xếp các đối tượng MessageEntity theo thứ tự offset giảm dần
        listEntity.sort((e1, e2) -> Integer.compare(e2.getOffset(), e1.getOffset()));

        for (MessageEntity entity : listEntity) {
            if ("text_link".equals(entity.getType()) && entity.getUrl() != null){
                String url = entity.getUrl();
                chatFwContent = replaceByOffSet(url, chatFwContent, entity.getOffset(), entity.getLength(), entity,listEntity);
            }
        }
        return chatFwContent;
    }

    private String replaceByOffSet(String url, String content, int offset, int length, MessageEntity currentEntity, List<MessageEntity> listEntity) {
        if (offset < 0 || offset >= content.length() || length < 0 || offset + length > content.length()) {
            throw new IllegalArgumentException("Invalid offset or length");
        }

        // Tạo đoạn text mới
        StringBuilder result = new StringBuilder(content);
        // Tính toán lại offset mới
        int replacedLengthDifference = url.length() - length;

        // Thay thế đoạn text
        result.replace(offset, offset + length, url);

        // Cập nhật lại offset cho các đối tượng MessageEntity sau
        for (MessageEntity entity : listEntity) {
            if (entity != currentEntity && entity.getOffset() > offset) {
                entity.setOffset(entity.getOffset() + replacedLengthDifference);
            }
        }

        return result.toString();
    }


    private String getSourceLinkMessage(String linkSourceChannel, long chatSourceMessId){
       return linkSourceChannel + "/" + chatSourceMessId;
   }

   private String getSourceLinkChannel(String userNameSource){
       return TELE_DOMAIN + userNameSource;
   }






}
