package com.scrapperapp.backend.scraper.model;

public class InputPostScrapper {

    private String keyword;


    public InputPostScrapper(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
