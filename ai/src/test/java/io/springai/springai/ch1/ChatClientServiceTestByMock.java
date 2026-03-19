package io.springai.springai.ch1;

import io.springai.springai.app.service.ch1.Ch1_ChatClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// 실제로 실행되어지는지 환경 테스트
@ExtendWith(MockitoExtension.class)
class ChatClientServiceTestByMock {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    ChatClient chatClient;

    @Mock
    ChatClient.Builder chatClientBuilder;

    Ch1_ChatClientService service;

    @BeforeEach
    void setUp() {
        // 🔑 반드시 먼저 stub
        when(chatClientBuilder.build()).thenReturn(chatClient);

        // 🔑 그리고 직접 생성
        service = new Ch1_ChatClientService(chatClientBuilder);
    }

    @Test
    void chatFewShot() {

        String json = """
                [
                  {
                    "name": "테스트 맛집",
                    "menu": ["김치찌개", "된장찌개"],
                    "address": "서울 종로구"
                  }
                ]
                """;

        when(chatClient.prompt()
                .user(anyString())
                .call()
                .content())
                .thenReturn(json);

        String result = service.chatFewShot("서울 종로 맛집 알려줘");

        assertThat(result).contains("테스트 맛집");
    }

    @Test
    void chat() {

        when(chatClient.prompt()              // chatClient 로 프롬프트 생성 시작
                .system(anyString())          // system 메시지는 아무 문자열이나 허용
                .user(anyString())            // user 메시지도 아무 문자열이나 허용
                .call()                       // 모델 호출 수행
                .content())                   // 호출 결과의 content 추출
                .thenReturn("테스트 응답입니다"); // 위 호출 결과로 반환할 값 지정

        String result = service.chat("안녕"); // 실제 테스트 대상 메서드 실행

        assertThat(result).isEqualTo("테스트 응답입니다"); // 기대한 응답과 같은지 확인
    }

    @Test
    void chatStream() {

        when(chatClient.prompt()                  // chatClient 로 프롬프트 생성 시작
                .system(anyString())              // system 메시지는 아무 문자열이나 허용
                .user(anyString())                // user 메시지도 아무 문자열이나 허용
                .stream()                         // 스트리밍 방식으로 응답을 받도록 설정
                .content())                       // 스트리밍 응답의 content Flux 추출
                .thenReturn(Flux.just("안녕", "하세요")); // "안녕", "하세요" 순서로 방출되는 Flux 반환

        StepVerifier.create(service.chatStream("인사")) // 서비스의 chatStream("인사") 결과 검증 시작
                .expectNext("안녕")                     // 첫 번째로 "안녕"이 방출되는지 확인
                .expectNext("하세요")                   // 두 번째로 "하세요"가 방출되는지 확인
                .verifyComplete();                     // 정상적으로 완료 신호까지 오는지 검증
    }
}

