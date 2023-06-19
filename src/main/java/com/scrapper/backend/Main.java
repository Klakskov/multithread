package com.scrapper.backend;

import com.scrapper.backend.scraper.model.PostScrapperDto;
import com.scrapper.backend.scraper.service.DoScrapper;
import com.scrapper.backend.scraper.service.GetScrapperService;
import com.google.gson.Gson;
import com.scrapper.backend.request.service.IRequestService;
import com.scrapper.backend.request.service.RequestService;
import com.scrapper.backend.scraper.controller.IScrapperController;
import com.scrapper.backend.scraper.controller.ScrapperController;
import com.scrapper.backend.scraper.fakeDatabase.Database;
import com.scrapper.backend.scraper.fakeDatabase.FakeDatabase;
import com.scrapper.backend.scraper.model.GetScrapperDto;
import com.scrapper.backend.scraper.model.InputPostScrapper;
import com.scrapper.backend.scraper.service.CreateScrapperService;
import com.scrapper.backend.scraper.service.interfaces.ICreateScrapperService;
import com.scrapper.backend.scraper.service.interfaces.IDoScrapper;
import com.scrapper.backend.scraper.service.interfaces.IGetScrapperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    private static IScrapperController scrapperController;
    private static Gson gson;

    public static void main(String[] args) {

        initialize(args);
        get("/crawl/:id", (req, res) ->{
                    res.type("json");

                    //"GET /crawl/" + req.params("id")
                    String id = req.params("id");
                    if(id == null || id.length() != 8){
                        res.status(400);
                        res.body(
                                gson.toJson("wrong id informed, must has 8 alphanumeric characters")
                        );
                        return res;
                    }

                    GetScrapperDto dto = scrapperController.getScrapperById(id);

                    res.status(200);
                    res.body(gson.toJson(dto));

                    return res;

                }
        );
        post("/crawl", (req, res) -> {
                    res.type("json");
                    //"POST /crawl" + System.lineSeparator() + req.body()
                    res.type("application/json");
                    String body = req.body();
                    if(body == null){
                        res.status(400);
                        res.body("Nao foi enviado keyword para busca!");
                        return res;
                    }
                    InputPostScrapper input = gson.fromJson(body, InputPostScrapper.class );

                    if(input.getKeyword() == null
                            || input.getKeyword().isBlank()
                            || input.getKeyword().length() < 4
                            || input.getKeyword().length() > 32){
                        res.status(400);
                        res.body(
                                gson.toJson("Keyword attribute must have at least 4 and maximum 32 characters")
                        );
                        return res;
                    }
                    PostScrapperDto dto = scrapperController.createScrapper(input);


                    res.status(201);
                    res.body(gson.toJson(dto));

                    return res;

                }
        );
    }


    private static void initialize(String[] args){

        Map<String, String> argsMap = new LinkedHashMap<>();
        for (String arg: args) {
            String[] parts = arg.split("=", 2);

            if(parts.length != 2){
                log.error("cant decode argument arg {}", arg);
                continue;
            }

            argsMap.put(parts[0].replace("--", ""), parts[1]);
        }

        String BASE_URL =  argsMap.get("BASE_URL") == null ? System.getenv("BASE_URL") :  argsMap.get("BASE_URL");

        if(BASE_URL == null || BASE_URL.isEmpty()){
            log.error("argument BASE_URL is mandatory !");
            throw new RuntimeException("cant initialize, BASE_URL argument is mandatory!");
        }
        IRequestService requestService = new RequestService();

        Database fakeDatabase = new FakeDatabase(new ConcurrentHashMap<>());

        IDoScrapper doScrapper = new DoScrapper();

        ICreateScrapperService createScrapperService = new CreateScrapperService(fakeDatabase, BASE_URL,
                doScrapper, requestService);

        IGetScrapperService getScrapperService = new GetScrapperService(fakeDatabase);

        scrapperController = new ScrapperController(createScrapperService, getScrapperService);
        gson = new Gson();

    }
}
