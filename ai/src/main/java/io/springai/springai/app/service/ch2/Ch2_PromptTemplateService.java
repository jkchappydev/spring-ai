package io.springai.springai.app.service.ch2;

import io.springai.springai.app.dto.ch2.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class Ch2_PromptTemplateService {

    private final ChatClient chatClient; // LLM 과 대화하기 위한 ChatClient

    private final String userQuestionTemplateText1 = """
                        {location} 지역에 {content} 정보를 5개이상 알려주세요
                        검색 후 시스템에 설정 된 언어로 변역된 내용만 출력 해줘
                    """; // 사용자 질문용 기본 템플릿 문자열,  location, content 값을 치환해서 사용, 결과는 시스템 언어 기준으로 번역되도록 요청

    // Template 모양이 JSON 형태일 경우 중괄호를 못 쓰므로 대괄호로 한다.
    private final String userQuestionTemplateText2 = """
                        <location> 지역에 <content> 정보를 5개이상 알려주세요
                        검색 후 시스템에 설정 된 언어로 변역된 내용만 출력 해줘
                    """; // 사용자 질문용 커스텀 구분자 템플릿 문자열,  < > 를 변수 치환 구분자로 사용, 결과는 시스템 언어 기준으로 번역되도록 요청

    private final String systemTemplateText1 = """
                        사용자의 검색 결과를 {language}로 변역해주세요 // language 값을 치환해서 사용
                    """; // system 메시지용 기본 템플릿 문자열

    // Template 모양이 JSON 형태일 경우 중괄호를 못 쓰므로 대괄호로 한다.
    private final String systemTemplateText2 = """
                        사용자의 검색 결과를 <language>로 변역해주세요 // < > 를 변수 치환 구분자로 사용
                    """; // system 메시지용 커스텀 구분자 템플릿 문자열

    private final PromptTemplate userQuestionTemplate1 = PromptTemplate.builder() // 기본 구분자({ })를 사용하는 사용자 질문 템플릿 생성
            .template(userQuestionTemplateText1) // 사용자 질문 템플릿 문자열 설정
            .build(); // PromptTemplate 생성 완료

    // Template 모양이 JSON 형태일 경우 중괄호를 못 쓰므로 대괄호로 한다.
    private final PromptTemplate userQuestionTemplate2 = PromptTemplate.builder() // 커스텀 구분자를 사용하는 사용자 질문 템플릿 생성
            .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build()) // 변수 치환 구분자를 < > 로 설정
            .template(userQuestionTemplateText2) // 사용자 질문 템플릿 문자열 설정
            .build(); // PromptTemplate 생성 완료

    private final PromptTemplate systemTemplate1 = PromptTemplate.builder() // 기본 구분자({ })를 사용하는 system 템플릿 생성
            .template(systemTemplateText1) // system 템플릿 문자열 설정
            .build(); // PromptTemplate 생성 완료

    // Template 모양이 JSON 형태일 경우 중괄호를 못 쓰므로 대괄호로 한다.
    private final PromptTemplate systemTemplate2 = PromptTemplate.builder() // 커스텀 구분자를 사용하는 system 템플릿 생성
            .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build()) // 변수 치환 구분자를 < > 로 설정
            .template(systemTemplateText2) // system 템플릿 문자열 설정
            .build(); // PromptTemplate 생성 완료

    // Constructor
    public Ch2_PromptTemplateService(ChatClient.Builder chatClientBuilder) { // ChatClient.Builder 를 주입받는 생성자
        this.chatClient = chatClientBuilder.build(); // Builder 로부터 ChatClient 생성
    }

    public String promptTemplate1(Question question) { // PromptTemplate 으로 Prompt 자체를 만들어 호출하는 방식
        Prompt prompt = userQuestionTemplate1.create(Map.of("location", question.location(), "content", question.content())); // 템플릿 변수 치환 후 Prompt 생성

        return chatClient
                .prompt(prompt) // 생성된 Prompt 를 그대로 전달
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    public String promptTemplate2(Question question) { // 템플릿을 문자열로 렌더링한 뒤 user 메시지로 전달하는 방식
        String userQuestion = userQuestionTemplate1.render(Map.of("location", question.location(), "content", question.content())); // 템플릿을 최종 문자열로 변환

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .user(userQuestion) // 렌더링된 사용자 질문 문자열 설정
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    public String promptTemplate3(Question question) { // user 템플릿 + system 템플릿을 각각 문자열로 렌더링해서 사용하는 방식
        String userQuestion  = userQuestionTemplate1.render(Map.of("location", question.location(), "content", question.content())); // 사용자 질문 문자열 생성
        String systemMessage  = systemTemplate1.render(Map.of("language", question.language())); // system 메시지 문자열 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .user(userQuestion) // 사용자 질문 설정
                .system(systemMessage) // system 메시지 설정
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    public String promptTemplate4(Question question) { // 템플릿으로 Message 객체를 직접 만들어 Prompt 에 담아 호출하는 방식
        Message userQuestionMessage  = userQuestionTemplate1.createMessage(Map.of("location", question.location(), "content", question.content())); // 사용자 질문 Message 생성
        Message systemMessage  = systemTemplate1.createMessage(Map.of("language", question.language())); // system Message 생성
        Prompt prompt = new Prompt(List.of(userQuestionMessage, systemMessage)); // 두 Message 를 묶어 Prompt 생성

        return chatClient.prompt(prompt) // 생성한 Prompt 전달
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

    public String promptTemplate5(Question question) { // 템플릿으로 Message 객체를 만든 뒤 messages(...) 로 직접 전달하는 방식
        Message userQuestionMessage  = userQuestionTemplate1.createMessage(Map.of("location", question.location(), "content", question.content())); // 사용자 질문 Message 생성
        Message systemMessage  = systemTemplate1.createMessage(Map.of("language", question.language())); // system Message 생성

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .messages(userQuestionMessage,systemMessage) // Message 객체들을 직접 추가
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

}
