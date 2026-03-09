package io.springai.springai.app.service.ch1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class Ch1_ChatClientMemoryService {

    private final ChatClient chatClientMemory;

    // Constructor
    public Ch1_ChatClientMemoryService(ChatClient.Builder chatClientBuilder) {
        ChatMemory chatMemory =MessageWindowChatMemory.builder()
                .maxMessages(3) // 대화 내용 저장 갯수
                .build();
        this.chatClientMemory = chatClientBuilder // ChatClient 객체를 만들 때 Advisors 를 추가로 추가하게 된다.
                .defaultAdvisors(
                        //    MessageChatMemoryAdvisor: 메모리에서 대화 내역을 검색하여 메시지 모음으로 프롬프트에 포함합니다.
                        //    PromptChatMemoryAdvisor: 메모리에서 대화 내역을 검색하여 시스템 프롬프트에 일반 텍스트로 추가합니다.
                        //    VectorStoreChatMemoryAdvisor: 벡터 저장소에서 대화 내역을 검색하여 시스템 메시지에 일반 텍스트로 추가합니다.
                        //MessageChatMemoryAdvisor.builder(chatMemory).build())
                        PromptChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public Flux<String> chatMemory(String question, String conversationId) { // 대화 메모리를 사용하는 스트리밍 채팅 메서드
        return chatClientMemory.prompt() // 메모리 기능이 적용된 ChatClient 로 프롬프트 생성 시작
                .user(question) // 사용자가 입력한 질문을 user 메시지로 전달
                .advisors(advisorSpec -> advisorSpec.param( // advisor 에 추가 파라미터를 설정
                        // 메모리에 저장 하려고 할 때 여러 사용자가 동시에 LLM 을 연동을 한다고 하면
                        // 각각의 사용자별로 메모리를 저장해야 한다.
                        // 이렇게 하기 위해서 conversationId 를 ChatMemory.CONVERSATION_ID (Key) 에 넣어서 구분하겠다.
                        // 즉, 동시에 여러 명이 대화를 할 때 각각의 사용자들별로 Conversation ID 값을 분리해서 저장을 하겠다. (현재 여기서는 Session ID를 가지고 진행)
                        ChatMemory.CONVERSATION_ID, conversationId // 대화별 메모리를 구분하기 위한 conversationId 지정
                ))
                .stream() // 응답을 스트리밍 방식으로 수신
                .content(); // 스트리밍 응답에서 실제 텍스트 내용만 추출
    }

}
