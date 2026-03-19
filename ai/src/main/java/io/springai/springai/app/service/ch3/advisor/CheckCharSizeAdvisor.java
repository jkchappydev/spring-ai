package io.springai.springai.app.service.ch3.advisor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;

/**
 * 사용자 질문을 LLM 에 전달하기 전에 먼저 검사하는 Advisor
 * 이 클래스는 CallAdvisor 만 구현했기 때문에 call() 방식으로 호출될 때만 동작한다
 * 입력된 사용자 질문 길이가 2자 미만이면 예외를 발생시킨다
 * 발생한 예외는 GlobalExceptionHandler 에서 처리할 수 있다
 * stream() 방식에서도 사용하려면 StreamAdvisor 를 추가로 구현하고 adviseStream() 도 구현해야 한다
 */
@Slf4j
public class CheckCharSizeAdvisor implements CallAdvisor {

    @SneakyThrows // 체크 예외를 명시적으로 처리하지 않고 던질 수 있게 함
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) { // call() 요청 직전에 실행되는 메서드
        this.checkCharSize(chatClientRequest); // 사용자 입력 길이 검증 수행
        return callAdvisorChain.nextCall(chatClientRequest); // 검증 통과 시 다음 Advisor 또는 실제 모델 호출로 넘김
    }

    @Override
    public String getName() { // Advisor 이름 반환
        return this.getClass().getSimpleName(); // 현재 클래스명을 Advisor 이름으로 사용
    }

    @Override
    public int getOrder() { // Advisor 실행 우선순위 반환
        return Ordered.HIGHEST_PRECEDENCE; // 가장 먼저 실행되도록 높은 우선순위 지정
    }

    private void checkCharSize(ChatClientRequest chatClientRequest) throws Exception { // 사용자 입력 길이를 검사하는 메서드

        if (chatClientRequest.prompt().getUserMessage().getText().length() < 2) { // 사용자 메시지 길이가 2자 미만이면
            throw new Exception("Char size too short"); // 예외 발생
        }
    }

}
