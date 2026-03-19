package io.springai.springai.app.service.ch6;

import io.springai.springai.app.service.ch6.tools.time.DateTimeTools;
import io.springai.springai.app.service.ch6.tools.weather.CurrentWeatherTools;
import io.springai.springai.app.service.ch6.tools.weather.ForecastWeatherTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Ch6_TimeWeatherToolsService {

    private final ChatClient chatClient; // Tool Calling 옵션이 적용된 ChatClient
    private final ChatOptions chatOptions; // Tool Calling 에 사용할 공통 옵션

    // Constructor
    public Ch6_TimeWeatherToolsService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자
        ToolCallback[] toolCallbacks = ToolCallbacks.from(
                new DateTimeTools(),
                new CurrentWeatherTools(),
                new ForecastWeatherTools()
        ); // Tool 객체들을 ToolCallback 배열로 변환

        this.chatOptions = ToolCallingChatOptions.builder() // Tool Calling 전용 ChatOptions 생성 시작
                .toolCallbacks(toolCallbacks) // 사용할 ToolCallback 들 등록
                .build(); // ChatOptions 생성 완료

        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions) // 기본 옵션으로 Tool Calling 옵션 등록
                .build(); // ChatClient 생성
    }

    public String chat1(String question) { // 기본 Tool Calling 옵션이 적용된 ChatClient 로 질문하는 메서드
        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                //.tools(new DateTimeTools()) // 필요 시 개별 Tool 을 직접 지정할 수도 있음
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    // 예시: Prompt 입력 가능
    public String chat2(String question) { // 호출 시점에 Tool Calling 옵션을 명시적으로 다시 지정하는 메서드
        String answer = chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .options(this.chatOptions) // 현재 호출에 사용할 Tool Calling 옵션 지정
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
        return answer; // 최종 응답 반환
    }

}
