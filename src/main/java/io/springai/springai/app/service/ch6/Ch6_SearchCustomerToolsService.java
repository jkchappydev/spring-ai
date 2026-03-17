package io.springai.springai.app.service.ch6;

import io.springai.springai.app.service.ch6.tools.customer.CustomerStringTools;
import io.springai.springai.app.service.ch6.tools.customer.CustomerTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch6_SearchCustomerToolsService {

    private final ChatClient chatClient; // 고객 조회 Tool 과 연동할 ChatClient

    // Constructor
    public Ch6_SearchCustomerToolsService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 남길 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행되도록 우선순위 지정

        this.chatClient = chatClientBuilder
                .defaultAdvisors(customLogger) // 기본 Advisor 로 Logger Advisor 등록
                .build(); // ChatClient 생성
    }

    // JSON
    public String getCustomer(String question) { // CustomerTools 를 사용해서 고객 정보를 조회하는 메서드
        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .tools(new CustomerTools()) // 현재 호출에서 사용할 Tool 로 CustomerTools 등록
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    // String
    public String getCustomerString(String question) { // CustomerStringTools 를 사용해서 고객 정보를 문자열 형태로 조회하는 메서드
        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .tools(new CustomerStringTools()) // 현재 호출에서 사용할 Tool 로 CustomerStringTools 등록
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

}