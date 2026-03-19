package io.springai.springai.controller;

import io.springai.springai.app.service.ch8.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ch8")
@Slf4j
@RequiredArgsConstructor
// Chapter 8. RAG (Retrieval-Augmented Generation) Controller
public class Ch8Controller {

    // 1. ETL Pipeline - add
    // 1. ETL Pipeline - delete
    private final Ch8_EtlPipelineService ch8EtlPipelineService;
    // 2. RAG Chat
    private final Ch8_RagChatService ch8RagChatService;
    // 3. RAG Chat: template
    private final Ch8_RagChatPromptTemplateService ch8RagChatPromptTemplateService;
    // 4. Retrieval Augmentation Advisor
    private final Ch8_RetrievalAugmentationAdvisorService ch8RetrievalAugmentationAdvisorService;
    // 5. Compression Query Transformer
    private final Ch8_CompressionQueryTransformerService ch8CompressionQueryTransformerService;
    // 6. Rewrite Query Transformer
    private final Ch8_RewriteQueryTransformerService ch8RewriteQueryTransformerService;
    // 7. Translation Query Transformer
    private final Ch8_TranslationQueryTransformerService ch8TranslationQueryTransformerService;
    // 8. Multi Query Expander
    private final Ch8_MultiQueryExpanderService ch8MultiQueryExpanderService;

    // 1. ETL Pipeline - add
    @RequestMapping("/add-vector-store")
    public String addDocument(@RequestParam("type") String type,
                              @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        log.info("addDocument {} {}", type, attach.getOriginalFilename());
        return ch8EtlPipelineService.addVectorStore(type,attach);
    }

    // 1. ETL Pipeline - delete
    @RequestMapping("/clear-vector-store")
    public String deleteAllDocument(@RequestParam("type") String type) {
        log.info("deleteAllDocument");
        return ch8EtlPipelineService.clearVectorStore(type);
    }

    // 2. RAG Chat
    @RequestMapping("/rag-chat")
    public Flux<String> ragChat(@RequestParam("type") String type, @RequestParam("prompt") String question) {
        log.info("ragChat: {}", question);
        return ch8RagChatService.ragChat(question, type);
    }

    // 3. RAG Chat: template
    @RequestMapping("/rag-chat-prompt-template")
    public Flux<String> ragChatPromptTemplate(@RequestParam("type") String type, @RequestParam("prompt") String question) {
        log.info("ragChat: {} {}", question, type);
        return ch8RagChatPromptTemplateService.ragChat(question, type);
    }

    // 4. Retrieval Augmentation Advisor
    @RequestMapping("/raa-rag-chat")
    public Flux<String> retrievalAugmentationAdvisorChat(@RequestParam("type") String type, @RequestParam("prompt") String question) {
        log.info("ragChat: {}", question);
        return ch8RetrievalAugmentationAdvisorService.ragChat(question, type);
    }

    // 5. Compression Query Transformer
    @RequestMapping("/cqt-rag-chat")
    public Flux<String> compressionQueryTransformer(@RequestParam("type") String type, @RequestParam("prompt") String question, HttpSession session) {
        log.info("ragChat: {}", question);
        return ch8CompressionQueryTransformerService.ragChat(question, type, session.getId());
    }

    // 6. Rewrite Query Transformer
    @RequestMapping("/rqt-rag-chat")
    public Flux<String> rewriteQueryTransformer(@RequestParam("type") String type, @RequestParam("prompt") String question, HttpSession session) {
        log.info("ragChat: {}", question);
        return ch8RewriteQueryTransformerService.ragChat(question, type, session.getId());
    }

    // 7. Translation Query Transformer
    @RequestMapping("/tqt-rag-chat")
    public Flux<String> translationQueryTransformer(@RequestParam("type") String type, @RequestParam("prompt") String question, HttpSession session) {
        log.info("ragChat: {}", question);
        return ch8TranslationQueryTransformerService.ragChat(question, type, session.getId());
    }

    // 8. Multi Query Expander
    @RequestMapping("/mqe-rag-chat")
    public Flux<String> multiQueryExpander (@RequestParam("type") String type, @RequestParam("prompt") String question, HttpSession session) {
        log.info("ragChat: {}", question);
        return ch8MultiQueryExpanderService.ragChat(question, type, session.getId());
    }
}
