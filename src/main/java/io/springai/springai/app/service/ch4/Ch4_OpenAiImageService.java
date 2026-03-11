package io.springai.springai.app.service.ch4;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
public class Ch4_OpenAiImageService {

    private final ChatClient chatClient; // 이미지 분석용 대화를 처리할 ChatClient
    private final ImageModel imageModel; // 이미지 생성 요청을 처리할 ImageModel

    // System Message 생성
    String systemMessageText =""" 
          너는 이미지 분석가 입니다.
          사용자가 전송한 이미지를 기반으로 사용자의 질문에 맞게 분석하고 답변을 한국어로 하세요.
        """; // 이미지 분석 시 사용할 system 메시지 원본 문자열

    private final PromptTemplate systemPrompt = PromptTemplate.builder() // system 프롬프트 템플릿 생성 시작
            .template(systemMessageText) // system 메시지 문자열 설정
            .build(); // PromptTemplate 생성 완료

    // Constructor
    public Ch4_OpenAiImageService(ChatClient.Builder chatClientBuilder, ImageModel imageModel) { // ChatClient.Builder 와 ImageModel 을 주입받는 생성자
        this.imageModel = imageModel; // 주입받은 ImageModel 저장
        chatClient = chatClientBuilder.build(); // Builder 로부터 ChatClient 생성
    }

    // Text를 이미지 URL로 생성
    public String generateImageUrl(String description) { // 텍스트 설명을 기반으로 이미지 URL 을 생성하는 메서드
        return generateImage(description, "url") // 응답 포맷을 url 로 지정해 이미지 생성
                .getResult() // 생성 결과 추출
                .getOutput() // 실제 출력 이미지 정보 추출
                .getUrl(); // 생성된 이미지 URL 반환
    }

    // Text를 이미지 파일로 생성
    public String generateImageToText(String description) { // 텍스트 설명을 기반으로 Base64 이미지 데이터를 생성하는 메서드
        return generateImage(description, "b64_json") // 응답 포맷을 b64_json 으로 지정해 이미지 생성
                .getResult() // 생성 결과 추출
                .getOutput() // 실제 출력 이미지 정보 추출
                .getB64Json(); // Base64 형태 이미지 문자열 반환
    }

    // Text를 이미지로 생성 Format에 따라 URL 또는 Image 파일로 생성
    private ImageResponse generateImage(String description, String format) { // 텍스트 설명을 받아 이미지 생성 요청을 수행하는 공통 메서드
        ImageMessage imageMessage = new ImageMessage(description); // 이미지 생성용 프롬프트 메시지 생성

        OpenAiImageOptions imageOptions = OpenAiImageOptions.builder() // OpenAI 이미지 생성 옵션 설정 시작
                .model("dall-e-3") // 사용할 이미지 생성 모델 지정
                .responseFormat(format) // 응답 형식을 url 또는 b64_json 으로 지정
                .width(1024) // 생성 이미지 너비 지정
                .height(1024) // 생성 이미지 높이 지정
                .N(1) // 한 번에 생성할 이미지 개수 지정
                .build(); // 이미지 옵션 생성 완료

        List<ImageMessage> imageMessageList = List.of(imageMessage); // 이미지 메시지를 List 로 구성
        ImagePrompt imagePrompt = new ImagePrompt(imageMessageList, imageOptions); // 이미지 메시지와 옵션을 묶어 ImagePrompt 생성

        return imageModel.call(imagePrompt); // 이미지 생성 모델 호출 후 응답 반환
    }

    // Image 파일과 질문을 이용해 Image 분석
    public Flux<String> imageAnalysis(String question, String contentType, byte[] bytes) { // 이미지 파일과 질문을 함께 보내 분석 결과를 스트리밍으로 받는 메서드
        Message systemMessage  = systemPrompt.createMessage(); // 미리 정의한 system 프롬프트로 system 메시지 생성

        Media media = Media.builder() // 멀티모달 Media 객체 생성 시작
                .mimeType(MimeType.valueOf(contentType)) // 전달받은 content-type 문자열을 MimeType 으로 설정
                .data(new ByteArrayResource(bytes)) // 이미지 byte[] 를 Resource 형태로 감싸서 설정
                .build(); // Media 객체 생성 완료

        UserMessage userMessage = UserMessage.builder() // 사용자 메시지 생성 시작
                .text(question) // 사용자가 입력한 질문 텍스트 설정
                .media(media) // 함께 전달할 이미지 파일 설정
                .build(); // UserMessage 생성 완료

        return chatClient.prompt() // 새 프롬프트 생성 시작
                .messages(userMessage, systemMessage) // user 메시지와 system 메시지를 함께 전달
                .stream() // 스트리밍 방식으로 모델 호출
                .content(); // 스트리밍 응답의 텍스트 내용만 추출
    }

}
