package com.utm.search.engine.controller;

import com.utm.search.engine.model.SearchResponse;
import com.utm.search.engine.service.ScrapeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SearchController {

    private final ScrapeService scrapeService;

    @PostMapping("search")
    public List<SearchResponse> search(@RequestParam List<String> words) {
        return scrapeService.search(words);
    }
}
