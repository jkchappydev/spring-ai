package io.springai.springai.app.service.ch3;

import io.springai.springai.app.service.ch3.advisor.CheckCharSizeAdvisor;
import io.springai.springai.app.service.ch3.advisor.ReReadingAdvisor;
import io.springai.springai.app.service.ch3.advisor.SimpleLoggerAdvisorHigh;
import io.springai.springai.app.service.ch3.advisor.SimpleLoggerAdvisorLow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
public class Ch3_AdvisorService {

    private final ChatClient chatClient; // 공통 Advisor 가 적용된 기본 ChatClient
    private final ChatClient chatClientMemory; // 메모리 Advisor 가 적용된 ChatClient

    // Constructor
    public Ch3_AdvisorService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자

        // Logger Advisor - yml파일에서 반드시 debug로 셋팅 해야 출력 됨
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor( // 요청/응답 로그를 출력하는 기본 Logger Advisor 생성
                request -> "[SimpleLoggerAdvisor] Custom request: " + request.prompt().getUserMessage(), // 요청 로그 포맷 지정
                response -> "[SimpleLoggerAdvisor] Custom response: " + response.getResult(), // 응답 로그 포맷 지정
                Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행되도록 우선순위 지정

        // 입력되는 내용에 이상 문자 감지 Adviser
        SafeGuardAdvisor safeGuardAdvisor = new SafeGuardAdvisor( // 금지어를 검사하는 SafeGuardAdvisor 생성
                List.of("스미싱", "무기", "비밀번호"), // 차단할 금지어 목록
                "사용자의 질문에 문제가 있는 단어가 있으면 시스템에 요청 할수 없습니다.", // 금지어 감지 시 반환할 안내 문구
                Ordered.HIGHEST_PRECEDENCE); // 가장 먼저 실행되도록 우선순위 지정

        // ChatClient 를 사용하는 서비스에는 공통적으로 현재 3개의 Advisor 가 탑재되어 있음. SimpleLoggerAdvisor, CheckCharSizeAdvisor, SafeGuardAdvisor
        this.chatClient = chatClientBuilder
                .defaultAdvisors(customLogger, new CheckCharSizeAdvisor(), safeGuardAdvisor) // 공통 Advisor 3개를 기본 등록
                .build(); // 기본 ChatClient 생성

        ChatMemory chatMemory = MessageWindowChatMemory.builder() // 최근 메시지 몇 개만 기억하는 메모리 객체 생성 시작
                .maxMessages(3) // 최근 3개의 메시지만 유지
                .build(); // ChatMemory 생성 완료

        this.chatClientMemory = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()) // 메모리 기능을 적용하는 Advisor 등록
                .build(); // 메모리 전용 ChatClient 생성
    }

    public String chat(String question) { // 일반 단건 채팅 요청 메서드
        return chatClient.prompt() // 기본 ChatClient 로 프롬프트 생성 시작
                .advisors(new SimpleLoggerAdvisorLow(), new SimpleLoggerAdvisorHigh()) // 필요 시 개별 Logger Advisor 추가 가능
                //.advisors(new ReReadingAdvisor()) // 공통 Advisor 외에 개별적으로 ReReadingAdvisor 추가
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .call() // 동기 방식으로 모델 호출
                .content(); // 응답 본문만 추출
    }

    public Flux<String> chatStream(String question) { // 스트리밍 채팅 요청 메서드
        return chatClient.prompt() // 기본 ChatClient 로 프롬프트 생성 시작
                .advisors(new SimpleLoggerAdvisorLow(), new SimpleLoggerAdvisorHigh()) // 스트림 요청 시 개별 Logger Advisor 추가
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 조각만 추출
    }

    public Flux<String> chatMemory(String question, String conversationId) { // 대화 메모리를 사용하는 스트리밍 채팅 메서드
        return chatClientMemory.prompt() // 메모리 기능이 적용된 ChatClient 로 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .advisors(advisorSpec -> advisorSpec.param( // Advisor 에 추가 파라미터 설정
                        ChatMemory.CONVERSATION_ID, conversationId // 대화별 메모리를 구분하기 위한 conversationId 지정
                ))
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 조각만 추출
    }

}
