package com.scrapper.backend.scraper.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataQueuScrapper implements Runnable {
    private static Logger log = LoggerFactory.getLogger(DataQueuScrapper.class);


    private final ExecutorService executor = Executors.newFixedThreadPool(15);

    private final ConcurrentLinkedQueue<DoScrapper> proccessQueu;
    private final Object EMPTY_QUEUE = new Object();

    private boolean hasItens = false;


    public DataQueuScrapper() {
        this.proccessQueu = new ConcurrentLinkedQueue<DoScrapper>();
    }

    public void waitOnEmpty() throws InterruptedException {
        synchronized (EMPTY_QUEUE) {
            EMPTY_QUEUE.wait();
        }
    }

    public void notifyAllForEmpty() {
        synchronized (EMPTY_QUEUE) {
            EMPTY_QUEUE.notifyAll();
        }
    }

    public void add(DoScrapper scrapper) {
        proccessQueu.add(scrapper);
        EMPTY_QUEUE.notifyAll();
    }

    public DoScrapper getScrapperToProcces(){
        return proccessQueu.remove();
    }

    @Override
    public void run() {

    }
}
