package com.scrapperapp.backend.scraper.service;

import com.scrapperapp.backend.scraper.model.ScrapperWorker;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DoScrapper implements IDoScrapper {
    private static Logger log = LoggerFactory.getLogger(DoScrapper.class);

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);



    @Override
    public void doScrapper(final ScrapperWorker scrapperWorker) {
        executor.execute(scrapperWorker);
    }

}
