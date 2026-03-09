package io.springai.springai.app.service.ch2;

import io.springai.springai.app.dto.ch2.Contents;
import io.springai.springai.app.dto.ch2.Question;
import io.springai.springai.app.dto.ch2.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class Ch2_StructuredOutputConverterService {

    private final ChatClient chatClient; // LLM 과 대화하기 위한 ChatClient

    // src/main/resources 폴더에 prompts 폴더 생성 후 아래 prompt template 파일을 생성
    @Value("classpath:prompts/system-message-prompt-template.st") // system 메시지 템플릿 파일 리소스 주입
    private Resource systemResource;

    @Value("classpath:prompts/user-message-structured-output.st") // 단일 객체(Bean) 형태 응답용 user 템플릿 파일 리소스 주입
    private Resource userResource;

    @Value("classpath:prompts/user-message-structured-output-map-output.st") // Map 형태 응답용 user 템플릿 파일 리소스 주입
    private Resource userResourceMapOutput;

    @Value("classpath:prompts/user-message-structured-list-output.st") // List 형태 응답용 user 템플릿 파일 리소스 주입
    private Resource userResourceListOutput;

    // Constructor
    public Ch2_StructuredOutputConverterService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자
        this.chatClient = chatClientBuilder.build(); // Builder 로부터 ChatClient 생성
    }

    public Contents beanOutputConverter(Question question) { // LLM 응답을 Contents 객체로 변환하는 방식
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource); // system 템플릿 파일로 템플릿 객체 생성
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource); // 단일 객체 응답용 user 템플릿 객체 생성
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content())); // location, content 값을 치환해 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate.render(Map.of("language", question.language())); // language 값을 치환해 system 메시지 문자열 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessage) // system 메시지 설정
                .user(userQuestion) // user 메시지 설정
                .call() // 모델 호출 수행
                .entity(Contents.class); // 응답을 Contents.class 타입으로 변환
    }

    public List<String> listOutputConverter(Question question) { // LLM 응답을 문자열 리스트로 변환하는 방식
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource); // system 템플릿 파일로 템플릿 객체 생성
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResourceListOutput); // 리스트 응답용 user 템플릿 객체 생성
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content())); // location, content 값을 치환해 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate.render(Map.of("language", question.language())); // language 값을 치환해 system 메시지 문자열 생성

        //userQuestion += "검색 결과는 상점 이름만 조회해줘"; // 필요 시 추가 조건을 문자열로 덧붙일 수 있음

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessage) // system 메시지 설정
                .user(userQuestion) // user 메시지 설정
                .call() // 모델 호출 수행
                .entity(new ListOutputConverter(new DefaultConversionService())); // 응답을 List<String> 형태로 변환
    }

    public List<Shop> parameterizedTypeReference(Question question) { // 제네릭 타입 정보를 유지한 채 List<Shop> 으로 변환하는 방식
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource); // system 템플릿 파일로 템플릿 객체 생성
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource); // user 템플릿 파일로 템플릿 객체 생성
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content())); // location, content 값을 치환해 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate.render(Map.of("language", question.language())); // language 값을 치환해 system 메시지 문자열 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessage) // system 메시지 설정
                .user(userQuestion) // user 메시지 설정
                .call() // 모델 호출 수행
                .entity(new ParameterizedTypeReference<List<Shop>>() {}); // 응답을 List<Shop> 타입으로 변환
    }

    public Map<String, Object> mapOutputConverter(Question question) { // LLM 응답을 Map 형태로 변환하는 방식
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource); // system 템플릿 파일로 템플릿 객체 생성

        SystemPromptTemplate userQuestionTemplate = SystemPromptTemplate.builder() // Map 응답용 user 템플릿 객체를 빌더로 생성
                .resource(userResourceMapOutput) // Map 응답용 템플릿 파일 지정
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build()) // 변수 치환 구분자를 < > 로 설정
                .build(); // 템플릿 객체 생성 완료
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content())); // location, content 값을 치환해 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate.render(Map.of("language", question.language())); // language 값을 치환해 system 메시지 문자열 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessage) // system 메시지 설정
                .user(userQuestion) // user 메시지 설정
                .call() // 모델 호출 수행
                .entity(new MapOutputConverter()); // 응답을 Map<String, Object> 형태로 변환
    }

}
