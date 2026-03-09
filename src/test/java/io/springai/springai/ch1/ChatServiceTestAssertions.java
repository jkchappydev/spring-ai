package io.springai.springai.ch1;

import io.springai.springai.app.service.ch1.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ChatServiceTestAssertions {

    @Autowired
    ChatService chatService;

    // LLM 에서 올바른 답이 내려왔는지 테스트
    @Test
    void contextLoads() {
        var answer =
                chatService.requestText("프랑스의 수도가 어디야?");
        Assertions.assertThat(answer).isNotNull();
        Assertions.assertThat(answer).contains("파리");
    }
}
