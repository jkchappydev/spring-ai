package io.springai.springai.app.service.ch8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch8_RagChatService {

    private final ChatClient chatClient; // RAG 검색 Advisor 가 적용된 ChatClient

    //Constructor
    public Ch8_RagChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) { // ChatClient.Builder 와 VectorStore 를 주입받는 생성자
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 남길 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                0); // Advisor 실행 순서 지정

        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore) // VectorStore 기반 RAG 검색 Advisor 생성 시작
                .searchRequest(
                        SearchRequest.builder() // 검색 조건 생성 시작
                                .topK(3) // 유사한 문서 상위 3개까지 조회
                                .similarityThreshold(0.6) // 유사도 0.6 이상인 문서만 검색
                                .build()) // SearchRequest 생성 완료
                .order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 실행되도록 우선순위 지정
                .build(); // QuestionAnswerAdvisor 생성 완료

        this.chatClient = chatClientBuilder
                .defaultAdvisors(questionAnswerAdvisor, customLogger) // 기본 Advisor 로 RAG 검색 Advisor 와 Logger Advisor 등록
                .build(); // ChatClient 생성
    }

    public Flux<String> ragChat(String question, String type) { // type 조건에 맞는 문서만 대상으로 RAG 질의응답을 수행하는 메서드

        log.info("ragChat question={} type={}", question, type); // 질문과 type 값 로그 출력

        return this.chatClient.prompt() // 새 프롬프트 생성 시작
                .user(question) // 사용자 질문 설정
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == '%s'".formatted(type))) // RAG 검색 시 metadata 의 type 값으로 문서 필터링
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 내용만 추출
    }
}