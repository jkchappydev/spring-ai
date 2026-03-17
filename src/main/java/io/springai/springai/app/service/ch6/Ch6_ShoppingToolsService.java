package io.springai.springai.app.service.ch6;

import io.springai.springai.app.service.ch6.tools.shopping.ShoppingTools;
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

import java.util.Map;

@Service
@Slf4j
public class Ch6_ShoppingToolsService {

    private final ChatClient chatClient;

    // Constructor
    public Ch6_ShoppingToolsService(ChatClient.Builder chatClientBuilder) {
        ToolCallback[] toolCallbacks = ToolCallbacks.from(
                new ShoppingTools()
        );

        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .build();
        this.chatClient = chatClientBuilder
                .defaultOptions(chatOptions)
                .build();
    }

    public String getPropensity(String question, String userId) {
        return chatClient.prompt()
                .system("""
                userId를 기반으로 구매목록을 이용하여 질문에 대해서 답변해줘.
                주로 구매한 카테고리를 질문 하면 카테고리 정보만 문자로 답변해줘.
                고객이 주로 주문한 카테고리와 고객이 주문한 총 주문금액의 평균가격보다 낮은 제품을 고객에게 추천해줘.
                고객이 선호 하거나 좋아 할 만한 제품은 다음과 같은 형식으로만 출력해줘
                예시)
                1. 제품명: 가격, 카테고리, 색상
                ...
                """)
                .user(question)
                .toolContext(Map.of("userId",userId))
                .call()
                .content();
    }

}
