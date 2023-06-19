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

public class DoScrapper implements IDoScrapper {
    private static Logger log = LoggerFactory.getLogger(DoScrapper.class);

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(30);



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
                doScrapper, database
        );
        doScrapper(newWorker);


    }




}
