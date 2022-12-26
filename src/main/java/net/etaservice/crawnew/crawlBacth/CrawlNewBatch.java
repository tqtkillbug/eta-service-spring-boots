package net.etaservice.crawnew.crawlBacth;

import lombok.extern.slf4j.Slf4j;
import net.etaservice.crawnew.common.StringUtils;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.service.NewService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.transform.sax.SAXResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CrawlNewBatch {

    @Autowired
    private NewService newService;


    private static final String SOURCE_NEWS_KENH14 = "Kenh14";
    private static final String SOURCE_NEWS_GENK = "GenK";
    private static final String SOURCE_NEWS_CAFEBIZ = "CafeBiz";

    @Scheduled(fixedDelay = 3600000)
    public void scheduleFixedDelayTask() throws IOException {
         log.info("Crawl Data News one time one hours - " + new Date());
        try {
            getNewsKenh14();
            getNewsFromGenk();
            getNewsFromCafebiz();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void getNewsKenh14() throws IOException {
        log.info("START CRAWL FROM KENH14");
        List<New> listNewKenh14 = new ArrayList<>();
        Date date = new Date();
        String targetUrl = "https://kenh14.vn";
        Document doc = Jsoup.connect(targetUrl).get();
        Elements newMain = doc.selectXpath("//*[@id=\"bindRegionNews\"]/div[1]/div[1]");
        for (Element el : newMain) {
            String fullUrl = targetUrl + el.select("div.klwfn-left.fl > a").attr("href");
            String title = el.select("div.klwfn-left.fl > a").attr("title");
            String imageUrl = el.select("div.klwfn-left.fl > a > img").attr("src");
            New news = new New(title,SOURCE_NEWS_KENH14, fullUrl, imageUrl, date,"hi By TQT",1);
            listNewKenh14.add(news);
        }

        Elements newSub = doc.selectXpath("//*[@id=\"bindRegionNews\"]/div[1]/div[2]");
        for (Element el : newSub) {
            String fullUrl = targetUrl + el.select("div.klwfn-right.fr > a").attr("href");
            String title = el.select("div.klwfn-right.fr > a").attr("title");
            String imageUrl = el.select("div.klwfn-right.fr > a > img").attr("src");
            New news = new New(title,SOURCE_NEWS_KENH14, fullUrl, imageUrl, date,"hi By TQT",1);
            listNewKenh14.add(news);
        }


        Elements listNewsKenh14 = doc.selectXpath("//*[@id=\"admWrapsite\"]/div[2]/div[3]/div/div/div/div[2]/div/div[4]/ul/div[2]");
        for (Element el : listNewsKenh14) {
            Elements elm_row = el.getElementsByTag("li");
            for (Element elr : elm_row) {
                String stypeBackgroundImage = elr.select("div.knswli-left.fl > a").attr("style");
                List<String> imageUrl = StringUtils.extractUrls(stypeBackgroundImage);
                String urlLast = "";
                if (imageUrl.size() > 0) {
                    urlLast = imageUrl.get(0).replaceAll("/thumb_w/250", "/thumb_w/620");
                }
                String title = elr.select("div.knswli-right > h3 > a").html();
                String href = targetUrl + elr.select("div.knswli-right > h3 > a").attr("href");
                New news = new New(title,SOURCE_NEWS_KENH14, href, urlLast, date,"hi By TQT",1);
                listNewKenh14.add(news);
            }
        }

        List<New> listNewCrawled = new ArrayList<>();
        Date now = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        String dateString  = DateFormatUtils.format(now, "yyyy-MM-dd");
        listNewCrawled = newService.getListNewBySourceAndDate(SOURCE_NEWS_KENH14,dateString + "%");
        for (New new_c : listNewCrawled) {
            listNewKenh14.removeIf(new_s -> !new_c.isEmpty() || new_c.getUrlFull().equals(new_s.getUrlFull()) || new_c.getTitle().equals(new_s.getTitle()));
        }
        this.newService.saveAll(listNewKenh14);
        log.info(" --END --INSERT "+ listNewKenh14.size() + "  KENH14 NEWS   --");
    }

    public void getNewsFromGenk() throws IOException {
         log.info("START CRAWL FROM GENK");
        List<New> listNewGenK = new ArrayList<>();
        Date now = new Date();
        String targetUrl = "https://genk.vn";
        Document doc = Jsoup.connect(targetUrl).get();
        Element mainNew = doc.selectXpath("//*[@id=\"admWrapsite\"]/div/div[2]/div[1]/div/div[1]/div/div[1]/div").get(0);
        String fullUrl = targetUrl + mainNew.select("div.gknews_box > a").attr("href");
        String title = mainNew.select("div.gknews_box > a").attr("title");
        String styleBackGround = mainNew.select("div.gknews_box > a > i").attr("style");
        List<String> imageUrl = StringUtils.extractUrls(styleBackGround);
        String urlLast = "";
        if (imageUrl.size() > 0) {
            urlLast = imageUrl.get(0);
        }
        New news = new New(title,SOURCE_NEWS_GENK, fullUrl, urlLast, now,"hi By TQT",1);
        listNewGenK.add(news);

        Elements noteworthyNews = doc.selectXpath("//*[@id=\"admWrapsite\"]/div/div[2]/div[1]/div/div[2]/div/div/ul/li");
        for (Element el : noteworthyNews) {
            String fullUrls = targetUrl + el.select("div > a").attr("href");
            String titles = el.select("div > a").attr("title");
            String styleImage = el.select("div > a > i").attr("style");
            List<String> imageUrls = StringUtils.extractUrls(styleImage);
            urlLast = "";
            if (imageUrls.size() > 0) {
                urlLast = imageUrls.get(0);
            }
            New new2 = new New(titles,SOURCE_NEWS_GENK, fullUrls, urlLast, now,"hi By TQT",1);
            listNewGenK.add(new2);
        }

        Elements listNewCommon = doc.selectXpath("//*[@id=\"LoadListCate\"]/li");
        for (Element el : listNewCommon) {
            String fullUrls = targetUrl + el.select("div > a").attr("href");
            String titles = el.select("div > a").attr("title");
            String imageSrc = el.select("div.knswli-left.fl > a > img").attr("src");
            New new3 = new New(titles,SOURCE_NEWS_GENK, fullUrls, imageSrc, now,"hi By TQT",1);
            listNewGenK.add(new3);
        }

        List<New> listNewCrawled = new ArrayList<>();
        Date nowNonTime = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        String dateString  = DateFormatUtils.format(nowNonTime, "yyyy-MM-dd");
        listNewCrawled = newService.getListNewBySourceAndDate(SOURCE_NEWS_GENK,dateString + "%");
        for (New new_c : listNewCrawled) {
            listNewGenK.removeIf(new_s -> !new_c.isEmpty() || new_c.getUrlFull().equals(new_s.getUrlFull()) || new_c.getTitle().equals(new_s.getTitle()));
        }
        this.newService.saveAll(listNewGenK);
         log.info("END INSERT "+ listNewGenK.size() + "  GENK NEWS ");
    }

    public void getNewsFromCafebiz() throws IOException {
        List<New> listNewCafebiz = new ArrayList<>();
        Date date = new Date();
        String targetUrl = "https://cafebiz.vn";
        Document doc = Jsoup.connect(targetUrl).get();
        Elements mainNew = doc.selectXpath("/html/body/div[1]/div[2]/div[4]/div/div[1]/div/div[1]");
        Element elementMain = mainNew.get(0);
        String fullUrlMain = targetUrl + elementMain.select("div > a").attr("href");
        String titleMain = elementMain.select("div > a").attr("title");
        String backgroundMain = elementMain.select("div > a > i").attr("style");
        String imageUrl =  StringUtils.extractUrlsFirst(backgroundMain);
        New newsMain = new New(titleMain,SOURCE_NEWS_CAFEBIZ, fullUrlMain, imageUrl, date,"hi By TQT",1);
        listNewCafebiz.add(newsMain);

        Elements subMains = doc.selectXpath("//*[@id=\"form1\"]/div[4]/div/div[1]/div/div[1]/ul/li");
        for (Element subMain : subMains) {
            String fullUrlSub = targetUrl + subMain.select("div > a").attr("href");
            String title = subMain.select("div > a").attr("title");
            String background = subMain.select("div > a > i").attr("style");
            String imageUrlSub =  StringUtils.extractUrlsFirst(background);
            New newSub = new New(title,SOURCE_NEWS_CAFEBIZ, fullUrlSub, imageUrlSub, date,"hi By TQT",1);
            listNewCafebiz.add(newSub);
        }

        Elements listNew = doc.selectXpath("//*[@id=\"form1\"]/div[4]/div/div[3]/div[2]/div[1]/ul/li");
        for (Element item : listNew) {
            String fullUrlSub = targetUrl + item.select("div > a").attr("href");
            String title = item.select("div > a").attr("title");
            String background = item.select("div > a > i").attr("style");
            String imageUrlSub =  StringUtils.extractUrlsFirst(background);
            New newSub = new New(title,SOURCE_NEWS_CAFEBIZ, fullUrlSub, imageUrlSub, date,"hi By TQT",1);
            listNewCafebiz.add(newSub);
        }
        List<New> listNewCrawled = new ArrayList<>();
        Date nowNonTime = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
        String dateString  = DateFormatUtils.format(nowNonTime, "yyyy-MM-dd");
        listNewCrawled = newService.getListNewBySourceAndDate(SOURCE_NEWS_CAFEBIZ,dateString + "%");
        for (New new_c : listNewCrawled) {
            listNewCafebiz.removeIf(new_s -> !new_c.isEmpty() || new_c.getUrlFull().equals(new_s.getUrlFull()) || new_c.getTitle().equals(new_s.getTitle()));
        }
        this.newService.saveAll(listNewCafebiz);
         log.info(" END INSERT "+ listNewCafebiz.size() + "  CAFEBIZ NEWS ");

    }


}
