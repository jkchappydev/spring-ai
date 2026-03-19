package  io.springai.springai.app.service.ch1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ChatService {

    // Spring AI 가 제공하는 ChatModel 구현체 주입
    // 실제 LLM 호출은 이 객체를 통해 수행된다.
    private final ChatModel chatModel;

    // 생성자 주입
    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    // 일반 단건 응답 요청
    // 사용자의 질문을 받아 LLM 에 한 번 요청하고,
    // 최종 응답 문자열만 반환한다.
    public String requestText(String question){

        // 모델의 역할/응답 스타일을 정의하는 시스템 메시지
        // 여기서는 "한국어로 친절하게 답변"하도록 지시한다.
        SystemMessage systemMessage = SystemMessage.builder()
                .text("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.")
                .build();

        // 실제 사용자가 입력한 질문 메시지
        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .build();

        // 모델 호출 시 사용할 옵션
        // 현재는 별도 옵션 없이 기본값으로 사용
        ChatOptions chatOptions = ChatOptions.builder().build();

        // Prompt 에 시스템 메시지 + 사용자 메시지 + 옵션을 담아서
        // 동기 방식으로 한 번 호출한다.
        ChatResponse chatResponse = chatModel.call(
                Prompt.builder()
                        .messages(systemMessage, userMessage)
                        .chatOptions(chatOptions)
                        .build()
        );

        // 모델 응답에서 최종 assistant 메시지를 꺼낸다.
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();

        // 최종 답변 텍스트 반환
        return assistantMessage.getText();
    }

    // 스트리밍 응답 요청
    // 응답을 한 번에 받지 않고, 생성되는 조각(chunk) 단위로 반환한다.
    public Flux<String> requestTextStream(String question){

        // 시스템 메시지: 모델 응답 정책 정의
        SystemMessage systemMessage = SystemMessage.builder()
                .text("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.")
                .build();

        // 사용자 질문 메시지
        UserMessage userMessage = UserMessage.builder()
                .text(question)
                .build();

        // 호출 옵션 (기본값)
        ChatOptions chatOptions = ChatOptions.builder().build();

        // 스트리밍 방식으로 모델 응답을 받는다.
        // 반환값은 여러 개의 ChatResponse 를 순차적으로 방출하는 Flux
        Flux<ChatResponse> fluxResponse = chatModel.stream(
                Prompt.builder()
                        .messages(systemMessage, userMessage)
                        .chatOptions(chatOptions)
                        .build()
        );

        // 각 ChatResponse 에서 assistant 응답 텍스트만 꺼내서
        // Flux<String> 형태로 변환한다.
        return fluxResponse.map(chatResponse -> {
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String chunk = assistantMessage.getText();

            // 스트리밍 중 null 이 들어올 가능성에 대비한 방어 코드
            if (chunk == null) chunk = "";

            return chunk;
        });
    }
}