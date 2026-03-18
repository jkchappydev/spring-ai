package io.springai.springai.app.service.ch8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch8_RagChatPromptTemplateService {

    private final ChatClient chatClient; // 커스텀 RAG 프롬프트 템플릿이 적용된 ChatClient

    //Constructor
    public Ch8_RagChatPromptTemplateService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) { // ChatClient.Builder 와 VectorStore 를 주입받는 생성자
        // Logger Advisor - yml파일에서 반드시 debug로 셋팅 해야 출력 됨
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 남길 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                0); // Advisor 실행 순서 지정

        PromptTemplate customPromptTemplate = PromptTemplate.builder() // RAG 에서 사용할 커스텀 프롬프트 템플릿 생성 시작
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build()) // 템플릿 변수 구분자를 < > 로 설정
                .template("""
                        <query>
                        답변 정보는 아래와 같습니다.
	                     --------------------
			            <question_answer_context>
			            ---------------------
            
                        답변 정보가 없는 경우, 질문에 답하세요.                
                        1. 답변 정보가 없는 경우  "죄송하지만 모릅니다!!"러고 말하세요.
                        전체적인 답변은 다음 규칙에 따라 답변해줘
                        1. "맥락에 따라..." 또는 "제공된 정보..." 또는 "주어진 정보..."와 같은 진술은 피하세요.
                        """) // 검색된 문맥을 답변에 반영하도록 하는 커스텀 프롬프트 내용 설정
                .build(); // PromptTemplate 생성 완료

        QuestionAnswerAdvisor questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore) // VectorStore 기반 RAG 검색 Advisor 생성 시작
                .promptTemplate(customPromptTemplate) // 기본 템플릿 대신 커스텀 프롬프트 템플릿 적용
                .order(Ordered.HIGHEST_PRECEDENCE) // 가장 먼저 실행되도록 우선순위 지정
                .build(); // QuestionAnswerAdvisor 생성 완료

        this.chatClient = chatClientBuilder
                .defaultAdvisors(questionAnswerAdvisor, customLogger) // 기본 Advisor 로 RAG 검색 Advisor 와 Logger Advisor 등록
                .build(); // ChatClient 생성
    }

    public Flux<String> ragChat(String question, String type) { // type 조건에 맞는 문서만 대상으로 커스텀 프롬프트 기반 RAG 질의응답을 수행하는 메서드
        log.info("ragChat Service: {} {}", question, type); // 질문과 type 값 로그 출력

        return this.chatClient.prompt() // 새 프롬프트 생성 시작
                .system("친절하게 한국어로 답변해줘.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == '%s'".formatted(type))) // RAG 검색 시 metadata 의 type 값으로 문서 필터링
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 내용만 추출
    }
}