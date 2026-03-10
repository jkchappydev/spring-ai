package io.springai.springai.app.service.ch3.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

/**
 * Question을 LLM에 전달하기 이전에 동작, Stream 또는 일반 Text 처리 시 각각 호출
 * 전송 되어지는 내용과 LLM에서 전달 하는 답변 내용에 대한 Log 처리
 * 질문과 답변 내용에 대한 전체 내용을 Log로 출력 (메타 정보까지)
 * logback.xml에 선언된 내용에 따라 화면에도 출력 되며 Log 파일로도 생성
 */
@Slf4j
public class SimpleLoggerAdvisorHigh implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) { // 일반 call() 요청 시 동작
        logRequest(chatClientRequest); // 요청 객체 전체를 로그로 출력
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest); // 다음 Advisor 또는 실제 모델 호출 수행
        logResponse(chatClientResponse); // 응답 객체 전체를 로그로 출력
        return chatClientResponse; // 원본 응답 그대로 반환
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) { // stream() 요청 시 동작
        logRequest(chatClientRequest); // 요청 객체 전체를 로그로 출력
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest); // 다음 Advisor 또는 실제 스트리밍 호출 수행
        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse); // 스트림 응답을 모으면서 최종 응답 객체 전체를 로그로 출력
    }

    @Override
    public String getName() { // Advisor 이름 반환
        return this.getClass().getSimpleName(); // 현재 클래스명을 Advisor 이름으로 사용
    }

    @Override
    public int getOrder() { // Advisor 실행 순서 반환
        return Ordered.HIGHEST_PRECEDENCE + 1; // 높은 우선순위로 먼저 실행되도록 설정
    }

    private void logRequest(ChatClientRequest request) { // 요청 객체 전체를 로그로 출력하는 메서드
        log.info("SimpleLoggerAdvisorHigh request: {}", request); // ChatClientRequest 전체 정보 로그 출력
    }

    private void logResponse(ChatClientResponse chatClientResponse) { // 응답 객체 전체를 로그로 출력하는 메서드
        log.info("SimpleLoggerAdvisorHigh response: {}", chatClientResponse); // ChatClientResponse 전체 정보 로그 출력
    }

}
