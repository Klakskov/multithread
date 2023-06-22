package com.scrapper.backend.scraper.service;

import com.scrapper.backend.request.service.IRequestService;
import com.scrapper.backend.scraper.fakeDatabase.Database;
import com.scrapper.backend.scraper.model.ScrapperEntity;
import com.scrapper.backend.scraper.model.ScrapperWorker;
import com.scrapper.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

public class DoScrapper implements IDoScrapper {
    private static Logger log = LoggerFactory.getLogger(DoScrapper.class);

    private final ThreadPoolExecutor executor ;

    private final String regexIsLink = "href\\s*=\\s*\"(?<site>[^\"]*\\.html|[^.\"\\s]*)\"";

    public DoScrapper(int qtdThreads) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(qtdThreads); //variable, in my case 11 processors + 1
    }

    void doScrapper(final ScrapperWorker scrapperWorker) {
        executor.execute(scrapperWorker);
    }

    @Override
    public void sendToProccess(
            ScrapperEntity entity,
            String urlToProccess,
            String rootUrl,
            Vector<String> urlVisited,
            IRequestService requestService,
            IDoScrapper doScrapper,
            Database database) {

        urlVisited.add(urlToProccess);
        int qtd = entity.addUrlInAction();

        log.info("sending to procces url {} for keyword {} , with {} sites opened to visited",
                urlToProccess, entity.getKeyword(), qtd);
        ScrapperWorker newWorker = new ScrapperWorker(
                entity, urlToProccess, rootUrl,
                urlVisited, requestService,
                doScrapper, database,
                createPattern(entity.getKeyword(), regexIsLink)
        );
        doScrapper(newWorker);

    }


    private Pattern createPattern(String keyword, String regexIsLink) {
        // Joining the patterns with capturing groups \bword\b
        String combinedPattern = "((?<keyword>\\b" + keyword + "\\b)|(" + regexIsLink + "))";
        // Compiling the combined pattern
        return Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);
    }


}
