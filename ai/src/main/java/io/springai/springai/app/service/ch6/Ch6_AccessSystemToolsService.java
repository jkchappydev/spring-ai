package io.springai.springai.app.service.ch6;

import io.springai.springai.app.service.ch6.tools.card.AccessSystemTools;
import io.springai.springai.app.service.ch6.tools.card.IdCardTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch6_AccessSystemToolsService {

    private final ChatClient chatClient; // 이미지 분석 + 출입 시스템 Tool Calling 을 처리할 ChatClient

    // 시스템 메시지 생성
    String systemMessageText ="""
          너는 이미지 분석가 입니다.
        """; // 모델의 역할을 지정하는 system 메시지

    String userMessageText ="""
          사용자가 전송한 이미지를 기반으로 사용자의 질문에 맞게 분석하고 답변을 한국어로 하세요. 
          답변을 만들때는 숫자로만 알려줘
          숫자가 직원의 사번으로 사용되며 모든 직원의 사번과 일치하는지 검사 한다.
          사번이 일치하면 출입문을 연다.
          사번이 일치하지 않으면 출문을 열수 없다.
        """; // 이미지 분석 후 사번 판별 및 출입 처리 규칙을 전달하는 user 메시지

    // Constructor
    public Ch6_AccessSystemToolsService(ChatClient.Builder chatClientBuilder, ImageModel imageModel) { // ChatClient.Builder 와 ImageModel 을 주입받는 생성자
        chatClient = chatClientBuilder.build(); // Builder 로부터 ChatClient 생성
    }

    public String imageAnalysisText(String contentType, byte[] bytes) { // 전달받은 이미지에서 사번을 판별하고 출입 가능 여부를 처리하는 메서드

        Media media = Media.builder() // 멀티모달 Media 객체 생성 시작
                .mimeType(MimeType.valueOf(contentType)) // 전달받은 content-type 문자열을 MimeType 으로 설정
                .data(new ByteArrayResource(bytes)) // 이미지 byte[] 를 Resource 형태로 감싸서 설정
                .build(); // Media 객체 생성 완료

        UserMessage userMessage = UserMessage.builder() // 사용자 메시지 생성 시작
                .text(userMessageText) // 이미지 분석 및 사번 판별 규칙을 user 메시지로 설정
                .media(media) // 함께 전달할 이미지 파일 설정
                .build(); // UserMessage 생성 완료

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system(systemMessageText) // system 메시지 설정
                .messages(userMessage) // 이미지가 포함된 user 메시지 전달
                .tools(new AccessSystemTools(), new IdCardTools()) // 출입 처리 Tool 과 사번 확인 Tool 등록
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }

}
