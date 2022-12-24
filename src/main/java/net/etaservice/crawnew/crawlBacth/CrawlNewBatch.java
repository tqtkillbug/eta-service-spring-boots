package net.etaservice.crawnew.crawlBacth;

import net.etaservice.crawnew.common.StringUtils;
import net.etaservice.crawnew.model.New;
import net.etaservice.crawnew.service.NewService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CrawlNewBatch {

    @Autowired
    private NewService newService;

    private static final String SOURCE_NEWS_KENH14 = "Kenh14";
    private static final String SOURCE_NEWS_GENK = "GenK";


    @Scheduled(fixedDelay = 3600000)
    public void scheduleFixedDelayTask() throws InterruptedException, IOException {
        System.out.println("Cralw Data Tin Tuc 1  1 lần - " + new Date());
        getNewsKenh14();
    }


    public void getNewsKenh14() throws IOException {
        List<New> listNewKenh14 = new ArrayList<>();
        Date date = new Date();
        String targetUrl = "https://kenh14.vn/";
        Document doc = Jsoup.connect(targetUrl).get();
        System.out.println("Crawling News From Kenh 14.......");
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
        this.newService.saveAll(listNewKenh14);
        System.out.println("Crawled News From Kenh 14!");
    }

    public void getNewsFromGenk() throws IOException {
        System.out.println("----------------START CRAWL FROM GENK--------------------");
        List<New> listNewGenK = new ArrayList<>();
        Date now = new Date();
        String targetUrl = "https://genk.vn/";
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

        this.newService.saveAll(listNewGenK);
        System.out.println("----------------END CRAWL FROM GENK--------------------");

    }


}
