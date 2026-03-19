package io.springai.springai.app.service.ch3.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * Question 을 LLM 에 전달하기 이전에 동작, Stream 또는 일반 Text 처리 시 각각 호출
 * 전송 되어지는 내용과 LLM 에서 전달 하는 답변 내용에 대한 Log 처리
 * 질문과 답변 내용에 대한 Text 만 Log 로 출력
 * logback.xml 에 선언된 내용에 따라 화면에도 출력 되며 Log 파일로도 생성
 */
@Slf4j
public class SimpleLoggerAdvisorLow implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) { // 일반 call() 요청 시 동작
        logRequest(chatClientRequest); // 요청에 포함된 system / user 메시지 로그 출력
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest); // 다음 Advisor 또는 실제 모델 호출 수행
        logResponse(chatClientResponse); // 모델 응답 로그 출력
        return chatClientResponse; // 원본 응답 그대로 반환
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
                                                 StreamAdvisorChain streamAdvisorChain) { // stream() 요청 시 동작
        logRequest(chatClientRequest); // 요청에 포함된 system / user 메시지 로그 출력
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest); // 다음 Advisor 또는 실제 스트리밍 호출 수행
        return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse); // 스트림 응답을 모으면서 최종 응답 로그 출력
    }

    @Override
    public String getName() { // Advisor 이름 반환
        return this.getClass().getSimpleName(); // 현재 클래스명을 Advisor 이름으로 사용
    }

    @Override
    public int getOrder() { // Advisor 실행 순서 반환
        return Ordered.HIGHEST_PRECEDENCE + 3; // 높은 우선순위로 먼저 실행되도록 설정
    }

    private void logRequest(ChatClientRequest chatClientRequest) { // 요청 메시지를 로그로 출력하는 메서드
        String systemMessage = chatClientRequest.prompt().getSystemMessage().getText(); // system 메시지 텍스트 추출
        String userMessage = chatClientRequest.prompt().getUserMessage().getText(); // user 메시지 텍스트 추출
        log.info("System Message: {}, User Message: {}", systemMessage, userMessage); // system / user 메시지 로그 출력
    }

    private void logResponse(ChatClientResponse chatClientResponse) { // 응답 메시지를 로그로 출력하는 메서드
        ChatResponse chatResponse = chatClientResponse.chatResponse(); // ChatClientResponse 에서 ChatResponse 추출
        AssistantMessage firstGeneration = Objects.requireNonNull(chatResponse).getResult().getOutput(); // null 이 아니라고 보장한 뒤 assistant 응답 추출
        String content = firstGeneration.getText(); // 응답 텍스트만 추출
        log.info("Response Message: {}", content); // 응답 텍스트 로그 출력
    }
}