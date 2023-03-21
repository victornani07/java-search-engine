package com.utm.search.engine.service;

import com.utm.search.engine.model.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapeService {

    @Value("${search.engine.max-depth}")
    private int maxDepth = 2;

    @Value("${search.engine.max-number-of-pages}")
    private int maxNumberOfPages = 20;

    @Value("${search.engine.url-wikipedia}")
    private String url;
    private final List<String> visitedPages = new ArrayList<>();
    private final List<SearchResponse> searchResponses = new ArrayList<>();

    public List<SearchResponse> search(List<String> words) {
        crawl(1, url, words);
        return searchResponses;
    }

    private void crawl(
        int depth,
        String url,
        List<String> words
    ) {
        if (depth == maxDepth) {
            System.out.println("We have reached the maximum depth");
            return;
        }

        Document htmlDocument = requestAccess(url, visitedPages);
        if (htmlDocument == null) {
            System.out.println("The returned HTML Document is null.");
            return;
        }

        if (checkIfDocumentIsLoginPage(htmlDocument)) {
            return;
        }

        Elements descriptionSelector = htmlDocument.select("meta[name=description]");
        String description = descriptionSelector.size() != 0
            ? descriptionSelector.get(0).attr("content")
            : htmlDocument.title();

        System.out.println("Link: " + url);
        System.out.println(htmlDocument.title());
        System.out.println("Page description: " + description);
        boolean alreadyMatched = false;

        for (Element link : htmlDocument.getAllElements()) {
            if (!alreadyMatched && areWordsMatching(link.text(), words)) {
                SearchResponse searchResponse = SearchResponse.builder()
                    .title(htmlDocument.title())
                    .url(url)
                    .description(description)
                    .build();
                searchResponses.add(searchResponse);
                alreadyMatched = true;
            }
            if (link.is("a[href]")) {
                String nextLink = link.absUrl("href");
                if (!visitedPages.contains(nextLink) && visitedPages.size() <= maxNumberOfPages) {
                    ++depth;
                    crawl(depth, nextLink, words);
                }
            }
        }
    }

    private boolean areWordsMatching(String text, List<String> words) {
        String lowerCaseText = text.toLowerCase();
        for (String word : words) {
            if (lowerCaseText.contains(word.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private Document requestAccess(
        String url,
        List<String> visitedPages
    ) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();
            if (connection.response().statusCode() != 200) {
                return null;
            }

            visitedPages.add(url);
            return document;
        } catch(IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private boolean checkIfDocumentIsLoginPage(Document document) {
        String documentTitle = document.title().toLowerCase();
        return documentTitle.contains("login")
            || documentTitle.contains("log in")
            || documentTitle.contains("sign in")
            || documentTitle.contains("create account")
            || documentTitle.contains("make account")
            || documentTitle.contains("sign up");
    }
}
