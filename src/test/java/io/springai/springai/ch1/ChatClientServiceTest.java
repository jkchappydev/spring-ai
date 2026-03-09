package io.springai.springai.ch1;

import io.springai.springai.app.service.ch1.Ch1_ChatClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
@Slf4j
class ChatClientServiceTest {

    @Autowired
    Ch1_ChatClientService ch1ChatClientService;

    @Test
    void contextLoads() {
        String question = "천안에 맛집 알려줘";
        Flux<String> result = ch1ChatClientService.chatStream(question);
        result.collectList().block().stream().forEach(System.out::println);
        //String result = chatClientService.chat(question);
        //log.info("result:{}", result);
    }
}
