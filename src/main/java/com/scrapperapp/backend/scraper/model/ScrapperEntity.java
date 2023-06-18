package com.scrapperapp.backend.scraper.model;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ScrapperEntity {

    private String id;
    private StatusScrapper status;
    private ConcurrentLinkedQueue<String> urls;

    private String keyword;

    private volatile AtomicInteger countUrlInAction;

    public void setCountUrlInAction(AtomicInteger countUrlInAction) {
        this.countUrlInAction = countUrlInAction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusScrapper getStatus() {
        return status;
    }

    public void setStatus(StatusScrapper status) {
        this.status = status;
    }

    public ConcurrentLinkedQueue<String> getUrls() {
        return urls;
    }

    public void setUrls(ConcurrentLinkedQueue<String> urls) {
        this.urls = urls;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public boolean isLastUrl(){
        return countUrlInAction.get() == 0;
    }

    public int addUrlInAction(){
        return countUrlInAction.addAndGet(1);
    }

    public int decreaseUrlInAction() {
        return countUrlInAction.decrementAndGet();
    }
}
