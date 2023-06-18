package com.scrapperapp.backend.scraper.model;

import com.scrapperapp.backend.request.service.IRequestService;
import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrapperWorker implements Runnable{
    private static final Logger log = LoggerFactory.getLogger(ScrapperWorker.class);


    final ScrapperEntity entity;
    final String url;
    final String rootUrl;
    final Vector<String> urlVisited;

    private final static String regexIsLink = "href\\s*=\\s*\"(?<site>[^\"]*\\.html|[^.\"\\s]*)\"";
    private final IRequestService requestService;
    private final IDoScrapper doScrapper;
    private final Database database;



    public ScrapperWorker(ScrapperEntity entity,
                          String url,
                          String rootUrl,
                          Vector<String> urlVisited,
                          IRequestService requestService,
                          IDoScrapper doScrapper,
                          Database database) {
        this.entity = entity;
        this.url = url;
        this.rootUrl = rootUrl;
        this.urlVisited = urlVisited;
        this.requestService = requestService;
        this.doScrapper = doScrapper;
        this.database = database;
    }

    @Override
    public void run() {
        log.info("thred doing scrapper for url {} keyword {} ",
                url, entity.getKeyword());

        if(isVisitedUrl()) {
            log.info("URL visited {} for keyword {}, returning...",
                    url, entity.getKeyword());
            decreaseUrlInActionAndVerifyStatus();
            return;
        }
        urlVisited.add(url);

        String site = requestService.getUrlContent(url); //(?! "+ regexNotAllowedTypes+")$)

        String combinedPattern = "((?<keyword>" + entity.getKeyword() + ")|("+ regexIsLink + "))";  // Joining the patterns with capturing groups
        Pattern pattern = Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);  // Compiling the combined pattern
        Matcher matcher = pattern.matcher(site);  // Creating a matcher for the text


        boolean foundTwice = false;
        while (matcher.find()) {
            if (matcher.group("keyword") != null && foundTwice == false) { // found the keyword
                log.info("found keyword {} on the site ...", matcher);
                entity.getUrls().add(url);
                database.updateData(entity);
                foundTwice = true;
            } else if (matcher.group("site") != null) { // found another site
                String url = matcher.group("site");
                if(isAbsoluteLink(url)){
                    boolean isSameRoot = url.contains(this.url);
                    if(isSameRoot){
                        log.info("found same root link: {}  ...", url);
                        sendToProccess(url);
                    }
                }
                else{

                    log.info("thred found parcial link: {}  ...", url);
                    String urlComplete = getUrlComplete(this.url, rootUrl, url);
                    sendToProccess(urlComplete);

                }
            }
        }


        decreaseUrlInActionAndVerifyStatus();
    }

    private void sendToProccess(String urlComplete) {


        int qtd = addUrlToVisitAndSaveScrapper(entity);
        log.info("sending to procces url {} for keyword {} , with {} sites opened to visited",
                urlComplete, entity.getKeyword(), qtd);
        ScrapperWorker newWorker = new ScrapperWorker(
                entity, urlComplete, rootUrl,
                urlVisited, requestService,
                doScrapper, database
        );
        doScrapper.doScrapper(newWorker);
    }

    private void decreaseUrlInActionAndVerifyStatus() {
        //entity = database.getDtaById(entity.getId());
        int sitesToCheck = entity.decreaseUrlInAction();
        log.info("decreasing visitedSites on entity {}, sites to check {}, sites visited {}  ",
                entity.getId(), sitesToCheck,  urlVisited.size());


        if(sitesToCheck == 0){
            log.info("last url, setting STATUS as done ... sitess visited: {} ", urlVisited.size());
            entity.setStatus(StatusScrapper.DONE);
        }
        database.updateData(entity);

    }

    private boolean isVisitedUrl() {

          return urlVisited.contains(url);
/*        for(int i = 0; i < urlVisited.size(); i++){
            String siteVisited = urlVisited.get(i);
            if(siteVisited.equals(url)){
                log.info("url {} ja visitada {} ", url, siteVisited);
                return true;
            }

        }


        return false;*/

    }

    private static String getUrlComplete(String url, String rootUrl , String match) {
        if (match.startsWith("/")) {
            return url + match.replaceFirst("/", "");
        }
        if(match.startsWith("..")) {
            while (match.startsWith("..")) {
                url = url.endsWith("/") ? url.substring(0, url.lastIndexOf("/", url.length() -1 ) +1 )
                        : url.substring(0, url.lastIndexOf("/") +1 );
                match = match.replaceFirst("../", "");
            }

            return  url + match;
        }
        else {
            if (match.endsWith("html")) {
                return rootUrl + match;
            }

        }
        log.error("problem PROBLEM PROBLEM PROBLEM defning urlComplete! matcher: {} urlActual {} urlRoot {}",
                match, url, rootUrl);
        return url + match;
    }


    private int addUrlToVisitAndSaveScrapper(ScrapperEntity entity) {
        int qtd = entity.addUrlInAction();
        database.updateData(entity);
        return qtd;
    }

    private boolean isAbsoluteLink(String match) {
        return match.startsWith("http");
    }


}
