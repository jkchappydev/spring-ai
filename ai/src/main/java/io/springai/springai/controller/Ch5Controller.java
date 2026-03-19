package io.springai.springai.controller;

import io.springai.springai.app.service.ch1.Ch1_ChatClientService;
import io.springai.springai.app.service.ch5.Ch5_OpenAiAudioService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@RestController
@RequestMapping("/ch5")
@Slf4j
@RequiredArgsConstructor
// Chapter 5. Multimodality API – Audio and Speech Controller
public class Ch5Controller {

    // 1. Text to Speech
    // 2. Text to Speech: Chat
    // 3. Text to Speech: Chat Stream
    // 4. Speech to Text
    // 5. Speech to Text: Chat
    // 6. Speech to Text: Chat Voice
    final Ch5_OpenAiAudioService ch5OpenAiAudioService;
    // 5. Speech to Text: Chat
    final Ch1_ChatClientService ch1ChatClientService;

    // 1. Text to Speech
    @RequestMapping("/text-to-speech")
    public Map<String, String> textToSpeech(@RequestParam("prompt") String question) {
        log.info(question);
        return ch5OpenAiAudioService.textToSpeech(question);
    }

    // 2. Text to Speech: Chat
    @RequestMapping("/text-to-speech-chat")
    public Map<String, String> textToSpeechChat(@RequestParam("prompt") String question) {
        log.info(question);
        return ch5OpenAiAudioService.textToSpeechChat(question);
    }

    // 3. Text to Speech: Chat Stream
    @RequestMapping(value = "/text-to-speech-chat-stream")
    public void textToSpeechChatStream(@RequestParam("prompt") String question, HttpServletResponse response) throws IOException {
        log.info(question);
        Flux<byte[]> bytes =  ch5OpenAiAudioService.textToSpeechChatStream(question);
        OutputStream os = response.getOutputStream();

        ByteArrayOutputStream combined = new ByteArrayOutputStream();
        for (byte[] data : bytes.toIterable()) {
            combined.write(data);
        }
        os.write(combined.toByteArray());
        os.flush();
    }

    // 4. Speech to Text
    @RequestMapping(value = "/speech-to-text")
    public String speechToText(
            @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        return ch5OpenAiAudioService.speechToText(attach);
    }

    // 5. Speech to Text: Chat
    @RequestMapping(value = "/speech-to-text-chat")
    public Flux<String> speechToTextChat(
            @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        // 음성 데이터를 Text로 변환 한 후 다시 LLM에 전달 하여 응답 하녀 Flux로 전달
        String text = ch5OpenAiAudioService.speechToText(attach);
        return ch1ChatClientService.chatStream(text);
    }

    // 6. Speech to Text: Chat Voice
    @RequestMapping(value = "/speech-to-text-chat-voice")
    public Map<String, String> speechToTextChatVoice(
            @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        String text = ch5OpenAiAudioService.speechToText(attach);
        return ch5OpenAiAudioService.textToSpeechChat(text);
    }

}
