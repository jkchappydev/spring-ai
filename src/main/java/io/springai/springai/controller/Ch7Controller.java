package io.springai.springai.controller;

import io.springai.springai.app.service.ch7.Ch7_ChatJdbcService;
import io.springai.springai.app.service.ch7.Ch7_ChatPgvectorService;
import io.springai.springai.app.service.ch7.Ch7_HotelEmbeddingModelService;
import io.springai.springai.app.service.ch7.Ch7_TextEmbeddingModelService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ch7")
@Slf4j
@RequiredArgsConstructor
// Chapter 7. Embedding Model Controller
public class Ch7Controller {

    // 1. Text Embedding
    // 1. Text Embedding - add
    // 1. Text Embedding - delete
    final Ch7_TextEmbeddingModelService ch7TextEmbeddingModelService;
    // 2. Hotel Data Embedding
    // 2. Hotel Data Embedding - add
    // 2. Hotel Data Embedding - delete
    final Ch7_HotelEmbeddingModelService ch7HotelEmbeddingModelService;
    // 3. Chat Memory PGvector
    // 3. Chat Memory PGvector - delete
    // 3. Chat Memory PGvector - delete all
    final Ch7_ChatPgvectorService ch7ChatPgvectorService;
    // 4. Chat Memory JDBC
    // 4. Chat Memory JDBC - delete
    // 4. Chat Memory JDBC - delete all
    final Ch7_ChatJdbcService ch7ChatJdbcService;

    // 1. Text Embedding
    @RequestMapping("/text-embedding")
    public String textEmbedding(@RequestParam("prompt") String userPrompt, String section, String name) {
        log.info(userPrompt);
        return ch7TextEmbeddingModelService.similaritySearch(userPrompt).get(0).getText();
    }

    // 1. Text Embedding - add
    @RequestMapping("/add-texts")
    public String add() {
        return ch7TextEmbeddingModelService.addData();
    }

    // 1. Text Embedding - delete
    @RequestMapping("/delete-texts")
    public String delete() {
        return ch7TextEmbeddingModelService.deleteDate();
    }

    // 2. Hotel Data Embedding
    @RequestMapping("/hotel-embedding")
    public String hotelEmbedding(@RequestParam("prompt") String userPrompt, String section, String name) {
        log.info(userPrompt);
        return ch7HotelEmbeddingModelService.similaritySearch(userPrompt, section, name).get(0).getText();
    }

    // 2. Hotel Data Embedding - add
    @RequestMapping("/add-hotels")
    public String addHotels() {
        return ch7HotelEmbeddingModelService.addData();
    }

    // 2. Hotel Data Embedding - delete
    @RequestMapping("/delete-hotels")
    public String deleteHotels() {
        return ch7HotelEmbeddingModelService.deleteDate();
    }

    // 3. Chat Memory PGvector
    @RequestMapping("/chat-pgvector")
    public String chatPgvector(@RequestParam("prompt") String userPrompt, HttpSession  session) {
        return ch7ChatPgvectorService.chat(userPrompt, session.getId());
    }

    // 3. Chat Memory PGvector - delete
    @RequestMapping("/delete-chat-pgvector")
    public String deleteChatPgvector(HttpSession  session) {
        return ch7ChatPgvectorService.deleteChat(session.getId());
    }

    // 3. Chat Memory PGvector - delete all
    @RequestMapping("/delete-all-chat-pgvector")
    public String deleteAllChatPgvector() {
        return ch7ChatPgvectorService.deleteAllChat();
    }

    // 4. Chat Memory JDBC
    @RequestMapping("/chat-jdbc")
    public String chatJdbc(@RequestParam("prompt") String userPrompt, HttpSession  session) {
        return ch7ChatJdbcService.chat(userPrompt, session.getId());
    }

    // 4. Chat Memory JDBC - delete
    @RequestMapping("/delete-chat-jdbc")
    public String deleteChatJdbc(HttpSession  session) {
        return ch7ChatJdbcService.deleteChat(session.getId());
    }

    // 4. Chat Memory JDBC - delete all
    @RequestMapping("/delete-all-chat-jdbc")
    public String deleteAllChatJdbc() {
        return ch7ChatJdbcService.deleteAllChat();
    }

}
