package com.scrapper.backend.scraper.model;

import com.scrapper.backend.request.service.IRequestService;
import com.scrapper.backend.scraper.fakeDatabase.Database;
import com.scrapper.backend.scraper.service.interfaces.IDoScrapper;
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

    private final String regexIsLink = "href\\s*=\\s*\"(?<site>[^\"]*\\.html|[^.\"\\s]*)\"";
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
        try {
            log.info("thread doing scrapper for url {} keyword {} ",
                    url, entity.getKeyword());

            String site = requestService.getUrlContent(url);

            Pattern pattern = createPattern(entity.getKeyword(), regexIsLink);
            Matcher matcher = pattern.matcher(site);  // Creating a matcher for the text


            boolean foundTwice = false;
            while (matcher.find()) {
                if (matcher.group("keyword") != null && foundTwice == false) { // found the keyword
                    log.info("found keyword {} on the site ...", matcher.group("keyword"));
                    entity.getUrls().add(url);
                    database.updateData(entity);
                    foundTwice = true;
                } else if (matcher.group("site") != null) { // found another site
                    String url = matcher.group("site");
                    String urlComplete = getUrlComplete(this.url, rootUrl, url);
                    if (isValidUrlToProccess(urlComplete, urlVisited)) {
                        doScrapper.sendToProccess(entity,
                                urlComplete,
                                rootUrl,
                                urlVisited,
                                requestService,
                                doScrapper, database);
                    }
                }
            }
        }catch (Exception e){
            log.error("Exception occurred ...");
            throw e;
        }finally {
            decreaseUrlInActionAndVerifyStatus();
        }
    }

    private Pattern createPattern(String keyword, String regexIsLink) {
        // Joining the patterns with capturing groups
        String combinedPattern = "((?<keyword>" + keyword + ")|(" + regexIsLink + "))";
        // Compiling the combined pattern
        return Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
    }

    private boolean isValidUrlToProccess( String urlComplete, Vector<String> urlVisited) {
        return urlComplete != null
                && isNotVisitedUrl(urlComplete, urlVisited);
    }
    private boolean isNotVisitedUrl(String url, Vector<String> vector) {

        return ! vector.contains(url);

    }
    

    private void decreaseUrlInActionAndVerifyStatus() {
        int sitesToCheck = entity.decreaseUrlInAction();
        log.info("decreasing visitedSites on entity {}, sites to check {}, sites visited {}  ",
                entity.getId(), sitesToCheck,  urlVisited.size());


        if(sitesToCheck == 0 || entity.isLastUrl()){
            log.info("last url, setting STATUS as done ... sites visited: {} ", urlVisited.size());
            entity.setStatus(StatusScrapper.DONE);
            database.updateData(entity);
        }

    }



    private static String getUrlComplete(String url, String rootUrl , String match) {
        log.info("transforming url {} urlRoot {} and match {} into compelteUrl ...",
                url, rootUrl, match);
        if(isAbsoluteLink(match)){
            if(isSameRootLink(rootUrl, match)) {
                return match;
            }else{
                return null;
            }
        }
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
        log.error("problem PROBLEM PROBLEM PROBLEM defining urlComplete! matcher: {} urlActual {} urlRoot {}",
                match, url, rootUrl);
        return url + match;
    }

    private static boolean isSameRootLink(String rootUrl, String match) {
        return match.contains(rootUrl);
    }


    private static boolean isAbsoluteLink(String match) {
        return match.startsWith("http");
    }


}
