package io.springai.springai.app.service.ch8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch8_CompressionQueryTransformerService {

    private final Advisor retrievalAugmentationAdvisor; // 질의 압축 + 문서 검색 + 문맥 보강이 적용된 RAG Advisor
    private final ChatClient chatClient; // Logger Advisor 가 적용된 ChatClient

    // Constructor
    public Ch8_CompressionQueryTransformerService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) { // ChatClient.Builder 와 VectorStore 를 주입받는 생성자
        // Logger Advisor - yml파일에서 반드시 debug로 셋팅 해야 출력 됨
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 남길 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                Ordered.HIGHEST_PRECEDENCE); // 높은 우선순위로 실행되도록 설정

        retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder() // Retrieval Augmentation Advisor 생성 시작
                .queryTransformers(CompressionQueryTransformer.builder().chatClientBuilder(chatClientBuilder).build()) // 이전 대화 맥락을 반영해 질문을 압축/재구성하는 QueryTransformer 적용
                .documentRetriever(VectorStoreDocumentRetriever.builder() // VectorStore 에서 관련 문서를 검색할 Retriever 생성 시작
                        .topK(5) // 유사한 문서 상위 5개까지 조회
                        .similarityThreshold(0.6) // 유사도 0.6 이상인 문서만 검색
                        .vectorStore(vectorStore) // 검색 대상 VectorStore 지정
                        .build()) // DocumentRetriever 생성 완료
                .queryAugmenter(ContextualQueryAugmenter.builder() // 검색된 문맥을 질의에 보강하는 QueryAugmenter 생성 시작
                        .allowEmptyContext(true) // 검색 결과가 없어도 LLM 호출은 계속 진행하도록 허용
                        .build()) // QueryAugmenter 생성 완료
                .build(); // RetrievalAugmentationAdvisor 생성 완료

        this.chatClient = chatClientBuilder
                .defaultAdvisors(customLogger) // 기본 Advisor 로 Logger Advisor 등록
                .build(); // ChatClient 생성
    }

    public Flux<String> ragChat(String question, String type, String conversationId) { // 대화 맥락을 반영해 질문을 압축하고, type 조건으로 문서를 검색해 답변하는 메서드

        return this.chatClient.prompt() // 새 프롬프트 생성 시작
                .system("친절하게 한국어로 답변해줘.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .advisors(advisorSpec -> advisorSpec.param(
                        ChatMemory.CONVERSATION_ID, conversationId // 이전 대화 맥락을 구분하기 위한 conversationId 전달
                ))
                .advisors(retrievalAugmentationAdvisor) // 질의 압축 + 문서 검색 + 문맥 보강 Advisor 적용
                .advisors(a -> a.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, "type == '%s'".formatted(type))) // VectorStore 검색 시 metadata 의 type 값으로 문서 필터링
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 내용만 추출
    }
}