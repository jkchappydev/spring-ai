package io.springai.springai.app.service.ch3;

import io.springai.springai.app.dto.ch2.Contents;
import io.springai.springai.app.dto.ch2.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class Ch3_StructuredOutputConverterService {

    private final ChatClient chatClient; // Structured Output 검증 Advisor 가 적용된 ChatClient

    // src/main/resources 폴더에 prompts 폴더 생성 후 아래 prompt template 파일을 생성
    @Value("classpath:prompts/system-message-prompt-template.st") // system 메시지 템플릿 파일 리소스 주입
    private Resource systemResource;

    @Value("classpath:prompts/user-message-structured-output.st") // user 메시지 템플릿 파일 리소스 주입
    private Resource userResource;

    // Constructor
    public Ch3_StructuredOutputConverterService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자
        StructuredOutputValidationAdvisor validationAdvisor = StructuredOutputValidationAdvisor.builder() // Structured Output 검증 Advisor 생성 시작
                .outputType(Contents.class) // 응답이 Contents.class 구조를 만족해야 함
                .maxRepeatAttempts(3) // 구조가 맞지 않으면 최대 3번까지 재시도
                .advisorOrder(BaseAdvisor.HIGHEST_PRECEDENCE + 1000) // Advisor 실행 순서 설정
                .build(); // 검증 Advisor 생성 완료

        this.chatClient = chatClientBuilder
                .defaultAdvisors(validationAdvisor) // 기본 Advisor 로 Structured Output 검증 Advisor 등록
                .build(); // ChatClient 생성
    }

    public Contents beanOutputConverter(Question question) { // LLM 응답을 Contents 객체로 변환하는 메서드
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource); // user 템플릿 파일로 템플릿 객체 생성
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource); // system 템플릿 파일로 템플릿 객체 생성
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content())); // location, content 값을 치환해 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate.render(Map.of("language", question.language())); // language 값을 치환해 system 메시지 문자열 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessage) // system 메시지 설정
                .user(userQuestion) // user 메시지 설정
                .call() // 모델 호출 수행
                .entity(Contents.class); // 응답을 Contents.class 타입으로 변환
    }

}
