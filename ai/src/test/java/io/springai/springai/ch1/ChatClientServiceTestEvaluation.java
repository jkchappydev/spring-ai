package io.springai.springai.ch1;

import io.springai.springai.app.service.ch1.Ch1_ChatClientService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// 응답되어지는 내용 퍙가
@SpringBootTest
class ChatClientServiceTestEvaluation {
    @Autowired
    Ch1_ChatClientService ch1ChatClientService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    }
    @Test
    void contextLoads() {
        String userText = "지금 몇시야 ?";
        String answer = ch1ChatClientService.chat(userText);
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                userText, answer);
        EvaluationResponse response = relevancyEvaluator
                .evaluate(evaluationRequest);

        Assertions.assertThat(response.isPass())
                .withFailMessage("""
          ========================================
          답변은 "%s"
          질문과 관련이 없는 것으로 간주됩니다
          "%s".
          ========================================
          """, answer, userText)
                .isTrue();
    }


}


