package io.springai.springai.app.service.ch8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch8_RetrievalAugmentationAdvisorService {

    private final ChatClient chatClient; // Retrieval Augmentation 용 Advisor 를 적용해서 사용할 ChatClient
    private final Advisor retrievalAugmentationAdvisor; // 검색된 문서를 기반으로 질의를 보강하는 RAG 전용 Advisor

    //Constructor
    public Ch8_RetrievalAugmentationAdvisorService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) { // ChatClient.Builder 와 VectorStore 를 주입받는 생성자

        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 남길 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                0); // Advisor 실행 순서 지정

        this.chatClient = chatClientBuilder
                .defaultAdvisors(customLogger) // 기본 Advisor 로 Logger Advisor 등록
                .build(); // ChatClient 생성

        retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder() // Retrieval Augmentation Advisor 생성 시작
                .documentRetriever(VectorStoreDocumentRetriever.builder() // VectorStore 에서 관련 문서를 검색할 Retriever 생성 시작
                        .topK(3) // 유사한 문서 상위 3개까지 조회
                        .similarityThreshold(0.6) // 유사도 0.6 이상인 문서만 검색
                        .vectorStore(vectorStore) // 검색 대상 VectorStore 지정
                        .build()) // DocumentRetriever 생성 완료
                // vector store에 없는 경우에도  LLM에 전송을 하도록 함.
                .queryAugmenter(ContextualQueryAugmenter.builder() // 검색된 문맥을 질의에 보강하는 QueryAugmenter 생성 시작
                        .allowEmptyContext(true) // 검색 결과가 없어도 LLM 호출은 계속 진행하도록 허용
                        .build()) // QueryAugmenter 생성 완료
                .build(); // RetrievalAugmentationAdvisor 생성 완료
    }

    public Flux<String> ragChat(String question, String type) { // type 조건에 맞는 문서를 검색해서 질의를 보강한 뒤 답변하는 메서드

        return this.chatClient.prompt() // 새 프롬프트 생성 시작
                .system("친절하게 한국어로 답변해줘.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .advisors(retrievalAugmentationAdvisor) // Retrieval Augmentation Advisor 적용
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "type == '%s'".formatted(type))) // VectorStore 검색 시 metadata 의 type 값으로 문서 필터링
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 내용만 추출
    }

}