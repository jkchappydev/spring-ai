package io.springai.springai.app.service.ch5;

import lombok.extern.slf4j.Slf4j; // log 객체 자동 생성을 위한 Lombok 어노테이션
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt; // 음성 파일을 텍스트로 변환할 때 사용하는 프롬프트 객체
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse; // 음성 -> 텍스트 변환 응답 객체
import org.springframework.ai.audio.tts.TextToSpeechPrompt; // 텍스트를 음성으로 변환할 때 사용하는 프롬프트 객체
import org.springframework.ai.audio.tts.TextToSpeechResponse; // 텍스트 -> 음성 변환 응답 객체
import org.springframework.ai.chat.client.ChatClient; // LLM 과 대화하기 위한 ChatClient
import org.springframework.ai.openai.OpenAiAudioSpeechModel; // OpenAI TTS(Text To Speech) 모델
import org.springframework.ai.openai.OpenAiAudioSpeechOptions; // OpenAI TTS 옵션 설정 객체
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel; // OpenAI STT(Speech To Text) 모델
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions; // OpenAI STT 옵션 설정 객체
import org.springframework.ai.openai.api.OpenAiAudioApi; // OpenAI 오디오 API 관련 enum / 타입 제공
import org.springframework.core.io.FileSystemResource; // 파일 시스템 경로를 Resource 로 다루기 위한 객체
import org.springframework.core.io.Resource; // 리소스 추상화 타입
import org.springframework.stereotype.Service; // 스프링 서비스 빈 등록
import org.springframework.web.multipart.MultipartFile; // 업로드된 음성 파일 타입
import reactor.core.publisher.Flux; // 스트리밍 응답 반환 타입

import java.io.IOException; // 파일 처리 예외
import java.nio.file.Files; // 임시 파일 생성용 유틸
import java.nio.file.Path; // 파일 경로 타입
import java.util.Base64; // byte[] 를 Base64 문자열로 변환할 때 사용
import java.util.HashMap; // 결과를 Map 으로 담을 때 사용
import java.util.Map; // key-value 결과 반환용 Map

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch5_OpenAiAudioService {

    private final ChatClient chatClient; // 텍스트 질의응답용 ChatClient

    private final OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel; // 음성 파일을 텍스트로 변환하는 STT 모델

    private final OpenAiAudioTranscriptionOptions textOpentions; // STT 모델 호출 시 사용할 옵션

    private final OpenAiAudioSpeechModel openAiAudioSpeechModel; // 텍스트를 음성 파일로 변환하는 TTS 모델

    private final OpenAiAudioSpeechOptions speechOptions; // TTS 모델 호출 시 사용할 옵션

    // Constructor
    public Ch5_OpenAiAudioService(ChatClient.Builder chatClientBuilder,
                                  OpenAiAudioTranscriptionModel openAiAudioTranscriptionModel,
                                  OpenAiAudioSpeechModel openAiAudioSpeechModel) { // ChatClient.Builder, STT 모델, TTS 모델을 주입받는 생성자
        chatClient = chatClientBuilder.build(); // Builder 로부터 ChatClient 생성

        this.openAiAudioTranscriptionModel = openAiAudioTranscriptionModel; // 주입받은 STT 모델 저장
        this.textOpentions = OpenAiAudioTranscriptionOptions.builder() // STT 옵션 생성 시작
                .model("whisper-1") // 사용할 음성 인식 모델 지정
                .language("ko") // 입력 음성 언어를 한국어로 지정
                .build(); // STT 옵션 생성 완료

        this.openAiAudioSpeechModel = openAiAudioSpeechModel; // 주입받은 TTS 모델 저장
        this.speechOptions = OpenAiAudioSpeechOptions.builder() // TTS 옵션 생성 시작
                .model("gpt-4o-mini-tts") // 사용할 음성 합성 모델 지정
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA) // 사용할 음성 스타일 지정
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3) // 응답 음성 포맷을 MP3 로 지정
                .speed(1.0) // 음성 재생 속도 지정
                .build(); // TTS 옵션 생성 완료
    }

    // 음성 데이터를 입력 받아 Text로 변환
    public String speechToText(MultipartFile multipartFile) throws IOException { // 업로드된 음성 파일을 텍스트로 변환하는 메서드
        Path tempFile = Files.createTempFile("multipart-", multipartFile.getOriginalFilename()); // 업로드 파일을 임시 파일로 생성
        multipartFile.transferTo(tempFile); // 업로드된 파일 내용을 임시 파일로 저장
        Resource audioResource = new FileSystemResource(tempFile); // 임시 파일 경로를 Resource 로 감쌈

        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(audioResource, textOpentions); // 음성 인식용 프롬프트 생성

        AudioTranscriptionResponse response = openAiAudioTranscriptionModel.call(prompt); // STT 모델 호출 후 텍스트 응답 받음
        String text = response.getResult().getOutput(); // 변환된 텍스트 추출
        return text; // 최종 텍스트 반환
    }

    // Text 파일을 음성으로 변환
    public Map<String, String> textToSpeech(String text) { // 텍스트를 음성 데이터로 변환하는 메서드
        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text, speechOptions); // 텍스트 음성 변환용 프롬프트 생성

        TextToSpeechResponse response = openAiAudioSpeechModel.call(speechPrompt); // TTS 모델 호출 후 음성 byte[] 응답 받음
        byte[] bytes = response.getResult().getOutput(); // 생성된 음성 데이터를 byte[] 로 추출

        String base64Audio = Base64.getEncoder().encodeToString(bytes); // byte[] 를 Base64 문자열로 변환

        Map<String, String> result = new HashMap<>(); // 결과를 담을 Map 생성
        result.put("audio", base64Audio); // Base64 로 변환한 음성 데이터를 audio 키로 저장
        return result; // 결과 Map 반환
    }

    // Text를 음성으로 변환, 단 음성으로 변환 시 Stream을 통해 데이터를 받아 Flux로 전달
    public Flux<byte[]> textToSpeechChatStream(String question) { // 질문에 대한 답변을 만든 뒤 그 답변을 음성 스트림으로 반환하는 메서드
        String answerText = chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .call() // LLM 호출 수행
                .content(); // 텍스트 답변만 추출

        return openAiAudioSpeechModel.stream(answerText, speechOptions); // 텍스트 답변을 음성으로 스트리밍 변환하여 반환
    }

    // Question을 LLM에 전달하여 답변을 받고, 받은 답변을 음성으로 변환 다시 답변을 요청, 이후 답변에 대한 내용을 음성과 Text로 전달
    public Map<String, String> textToSpeechChat(String question) { // 질문을 받아 텍스트 답변과 음성 답변을 함께 반환하는 메서드

        String answerText = chatClient.prompt() // 새 프롬프트 생성 시작
                //.system("100자이내로 친절하게 답변해줘.") // 필요 시 짧은 답변 제한용 system 메시지 사용 가능
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .user(question) // 사용자 질문 설정
                .call() // LLM 호출 수행
                .content(); // 텍스트 답변만 추출

        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(answerText, speechOptions); // LLM 답변 텍스트를 음성 변환용 프롬프트로 생성
        TextToSpeechResponse response = openAiAudioSpeechModel.call(speechPrompt); // TTS 모델 호출 후 음성 데이터 응답 받음

        byte[] responseAsBytes = response.getResult().getOutput(); // 생성된 음성 데이터를 byte[] 로 추출
        String base64Audio = Base64.getEncoder().encodeToString(responseAsBytes); // byte[] 를 Base64 문자열로 변환

        Map<String, String> result = new HashMap<>(); // 최종 결과를 담을 Map 생성
        result.put("answer", answerText); // 텍스트 답변 저장
        result.put("audio", base64Audio); // 음성 데이터를 Base64 문자열로 저장

        return result; // 텍스트 + 음성 결과 반환
    }

}