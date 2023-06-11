package com.scrapperapp.backend.scraper.model;

import java.util.ArrayList;

public class ScrapperEntity {

    private String id;
    private StatusScrapper status;
    private ArrayList<String> urls;

    private String keyword;


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

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
