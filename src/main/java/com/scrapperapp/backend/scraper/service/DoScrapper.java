package com.scrapperapp.backend.scraper.service;

import com.scrapperapp.backend.request.service.IRequestService;
import com.scrapperapp.backend.request.service.RequestService;
import com.scrapperapp.backend.scraper.fakeDatabase.Database;
import com.scrapperapp.backend.scraper.model.ScrapperEntity;
import com.scrapperapp.backend.scraper.model.StatusScrapper;
import com.scrapperapp.backend.scraper.service.interfaces.IDoScrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoScrapper implements IDoScrapper {
    private static Logger log = LoggerFactory.getLogger(DoScrapper.class);

    private final String regexIsLink = "href\\s*=\\s*\"([^\"]*)\""; //

    private final IRequestService requestService;

    private final Database database;

    public DoScrapper(Database database) {
        this.database = database;
        this.requestService = new RequestService();
    }

    @Override
    public void doScrapper(final ScrapperEntity entity, final String url,
                           final String rootUrl,
                           ArrayList<String> urlVisited) {

        log.info("doing scrapper for url {} keyword {} ",
                url, entity.getKeyword());
        String isRepetitive = urlVisited.stream()
                .filter(ele -> ele.equals(url))
                .findFirst()
                .orElse(null);

        if(isRepetitive == null) {
            urlVisited.add(url);
        }

        String site = requestService.getUrlContent(url);

        String combinedPattern = "(" + entity.getKeyword() + ")|(" + regexIsLink + ")";  // Joining the patterns with capturing groups
        Pattern pattern = Pattern.compile(combinedPattern, Pattern.CASE_INSENSITIVE);  // Compiling the combined pattern

        Matcher matcher = pattern.matcher(site);  // Creating a matcher for the text


        boolean foundTwice = false;
        while (matcher.find()) {
            if (matcher.group(1) != null && foundTwice == false) { // found the keyword
                log.info("found keyword on the site ...");
                entity.getUrls().add(url);
                database.updateData(entity);
                foundTwice = true;
            } else if (matcher.group(3) != null) { // found another site
                String match = matcher.group(3);
                if(isAbsoluteLink(match)){
                    boolean isSameRoot = match.contains(url);
                    if(isSameRoot){
                        log.info("found same root link: {}  ...", match);
                        addUrlToVisitAndSaveScrapper(entity);
                        Thread newThread = new Thread(() -> {
                            IDoScrapper doScrapper = new DoScrapper(database);
                            doScrapper.doScrapper(entity, match, rootUrl, urlVisited);
                        });
                        newThread.start();
                    }

                }else{

                    log.info("found parcial link: {}  ...", match);
                    String urlComplete = getUrlComplete(url, rootUrl, match);
                    addUrlToVisitAndSaveScrapper(entity);

                    Thread newThread = new Thread(() -> {
                        IDoScrapper doScrapper = new DoScrapper(database);
                        doScrapper.doScrapper(entity, urlComplete, rootUrl, urlVisited);
                    });
                    newThread.start();

                }
            }
        }

        //entity = database.getDtaById(entity.getId());
        log.info("decreasing visitedSites on entity {}, is last url ? {} ",
                entity.getId(), entity.isLastUrl());
        entity.decreaseVisitedSite();

        if(entity.isLastUrl()){
            log.info("last url, setting STATUS as done ...");
            entity.setStatus(StatusScrapper.DONE);
        }
        database.updateData(entity);
    }

    private static String getUrlComplete(String url, String rootUrl , String match) {
        if (match.startsWith("/")) {
            return url + match.replaceFirst("/", "");
        } else {
            if (match.endsWith("html")) {
                return rootUrl + match;
            }

        }
        log.error("problem PROBLEM PROBLEM PROBLEM defning urlComplete! matcher: {} urlActual {} urlRoot {}",
                match, url, rootUrl);
        return url + match;
    }


    private void addUrlToVisitAndSaveScrapper(ScrapperEntity entity) {
        entity.addUrlToVisit();
        database.updateData(entity);
    }

    private boolean isAbsoluteLink(String match) {
        return match.startsWith("http");
    }


    public static void main(String[] args) {
        String example = """ 
?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><html xmlns="http://www.w3.org/1999/xhtml"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /><title>Linux manual pages</title><link rel="stylesheet" type="text/css" href="stylesheet/manpages.css" /><meta name="generator" content="DocBook XSL Stylesheets Vsnapshot" /><link rel="home" href="index.html" title="Linux manual pages" /><link rel="next" href="manpageindex.html" title="Manual page section index" /><script type="text/javascript" src="stylesheet/manpages.js"></script><link rel="icon" href="stylesheet/icon.gif" type="image/gif" /></head><body onload="javascript:init()"><div class="navheader"><table width="100%" summary="Navigation header"><tr><th colspan="3" align="center">Linux manual pages</th></tr><tr><td width="20%" align="left"> </td><th width="60%" align="center"> </th><td width="20%" align="right"> <a accesskey="n" href="manpageindex.html">Next</a></td></tr></table><hr /></div><div class="part"><div class="titlepage"><div><div><h1 class="title"><a id="index"></a>Linux manual pages</h1></div><div><p class="copyright">Copyright © 2007-2020 Sam Varshavchik</p></div><div><div class="legalnotice"><a id="idm45422340552864"></a><p>
                This is a compilation of Linux manual pages, converted to HTML.
        Permission is granted to copy, distribute and/or modify this
        compilation of Linux manual pages under
        the terms of the GNU Free Documentation License, Version 1.2;
        with the Invariant Sections being the license and copyright sections
        of each individual manual page,
                no Front-Cover texts, and no Back-Cover texts.
                A copy of the license is included in the section entitled
	<span class="quote">“<span class="quote"><a class="literalurl" href="gfdl.html" title="Appendix A. GNU Free Documentation License">GNU Free Documentation
        License</a></span>”</span>.</p><p>
                Note that individual manual pages have different authors and distribution
        terms.  This notice applies to this compilation of manual pages, as a whole.
        Individual manual pages may be redistributed as per their individual
        distribution terms.</p></div></div></div></div><div class="partintro"><div><div><div><h1 class="title"><a id="intro"></a>Introduction</h1></div></div></div><p>
                This compilation
        does not contain all manual pages on a typical Linux installation.
                Linux manual pages come from thousands of individual software packages,
                with each package typically installing one or two pages, each.
        This compilation includes core manual pages selected from the following
        sources:</p><div class="itemizedlist"><ul class="itemizedlist" style="list-style-type: disc; "><li class="listitem">
	  <a class="ulink" href="https://www.kernel.org/pub/linux/docs/man-pages/" target="_top"><span class="package">man-pages-5.04</span></a>,
                Linux core manual pages maintained by Michael Kerrisk</li><li class="listitem">
	  <a class="ulink" href="http://hiring.axreng.com/htmlman1/chgrp.1.html" target="_top"><span class="package">coreutils-8.31</span></a></li><li class="listitem">
	  <a class="ulink" href="htmlman1/chgrp.1.html" target="_top"><span class="package">util-linux-2.33.1</span></a></li><li class="listitem">
	  <a class="ulink" href="http://www.openldap.org" target="_top"><span class="package">OpenLDAP</span> 2.4.48</a></li><li class="listitem">
	  <a class="ulink" href="http://www.linux-pam.org/library/" target="_top"><span class="package">Linux-PAM</span> 1.3.0</a></li><li class="listitem">
	  <a class="ulink" href="http://www.PCRE.org/" target="_top"><span class="package">PCRE</span> 10.34</a></li></ul></div><p>
                You can
                <a class="literalurl" href="manpageindex.html" title="Manual page section index">browse this compilation of
        Linux manual pages online</a>, or download and peruse it at your leisure,
                by downloading an <a class="ulink" href="archive" target="_top">archive</a>.
    </p><p>
                In all cases, please grab
                (<a class="ulink" href="http://www.courier-mta.org/KEYS.bin" target="_top">my GPG key</a>),
        and keep it handy.</p><div class="section"><div class="titlepage"><div><div><h2 class="title" style="clear: both"><a id="feedback"></a>Feedback</h2></div></div></div><p>
                You should confirm the source of any errors in this documentation, before
        reporting them.
        Linux manual pages are a collective effort of thousands of individuals.
                In most cases, this compilation does not correct errors that are present
        in the original manual page text.</p><p>
                Before reporting any errors to me,
	&lt;<span class="emphasis"><em>mrsam@</em></span><span class="emphasis"><em>courier-mta.com</em></span>&gt;,
        check if the error exists is the same manual page in the
	<span class="emphasis"><em>same version</em></span> of the original package.
        If not, the error is mine.  I probably introduced it when I converted the
        manual page from its original troff source to <acronym class="acronym">HTML</acronym>.</p><p>
                But if the error also exists in the original manual page, notify the manual
        page's original author, unless a newer version of the original package is
        available, and the error is already fixed in the newer version.
                If the newer version fixes the error, the fix will be included when I
        prepare a new version of this manual page compilation.</p></div></div></div><div class="navfooter"><hr /><table width="100%" summary="Navigation footer"><tr><td width="40%" align="left"> </td><td width="20%" align="center"> </td><td width="40%" align="right"> <a accesskey="n" href="manpageindex.html">Next</a></td></tr><tr><td width="40%" align="left" valign="top"> </td><td width="20%" align="center"> </td><td width="40%" align="right" valign="top"> Manual page section index</td></tr></table></div></body></html>
                
                """;

        String string = "href\\s*=\\s*\"([^\"]*)\"";
        // "href\\s*=\\s*\"([^\"]*)\""
        String regex =  "(" + "protecao" + ")|(" + string + ")"; // this expression!
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(example);

        AtomicInteger cont = new AtomicInteger();
        boolean matchFound = matcher.find();
        if(matchFound){
            matcher.results().forEach( ele -> {
                        System.out.println("MAtcher >>> " + ele.group());
                        cont.getAndIncrement();
                    }
            );
        }

        System.out.println(cont.get());
        matcher.reset();
        cont.set(0);
        while ( matcher.find())
        {
            cont.getAndIncrement();
            if (matcher.group(1) != null) {
                String match = matcher.group(1);
                System.out.println("Pattern 1 match: " + match);
            } else if (matcher.group(3) != null) {
                String match = matcher.group(3);
                System.out.println("Pattern 2 match: " + match);
            }
        }

        System.out.println(cont.get());

    }


}
