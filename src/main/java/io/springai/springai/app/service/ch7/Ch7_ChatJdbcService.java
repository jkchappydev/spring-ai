package io.springai.springai.app.service.ch7;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch7_ChatJdbcService {

    private final ChatClient chatClient; // JDBC 기반 대화 메모리 Advisor 가 적용된 ChatClient

    private final JdbcChatMemoryRepository jdbcChatMemoryRepository; // Spring Boot 에 설정된 DB 와 연동되는 JDBC 메모리 저장소

    // Constructor
    public Ch7_ChatJdbcService(ChatClient.Builder chatClientBuilder,
                               JdbcChatMemoryRepository jdbcChatMemoryRepository) { // ChatClient.Builder 와 JdbcChatMemoryRepository 를 주입받는 생성자
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository; // 주입받은 JDBC 메모리 저장소 저장

        ChatMemory chatMemory = MessageWindowChatMemory.builder() // MessageWindowChatMemory 생성 시작
                .chatMemoryRepository(this.jdbcChatMemoryRepository) // 메모리 저장소로 JDBC Repository 지정
                .maxMessages(30) // 최근 30개의 메시지만 유지
                .build(); // ChatMemory 생성 완료

        this.chatClient = chatClientBuilder
                // MessageChatMemoryAdvisor: 메모리에서 대화 내역을 검색하여 메시지 모음으로 프롬프트에 포함합니다.
                // PromptChatMemoryAdvisor: 메모리에서 대화 내역을 검색하여 시스템 프롬프트에 일반 텍스트로 추가합니다.
                // VectorStoreChatMemoryAdvisor: 벡터 저장소에서 대화 내역을 검색하여 시스템 메시지에 일반 텍스트로 추가합니다.
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build()) // MessageChatMemoryAdvisor 를 기본 Advisor 로 등록
                .build(); // ChatClient 생성
    }

    public String deleteChat(String conversationId) { // 특정 conversationId 의 대화 내역만 삭제하는 메서드
        this.jdbcChatMemoryRepository.deleteByConversationId(conversationId); // conversationId 에 해당하는 대화 메모리 삭제
        return "Delete Completed "; // 삭제 완료 메시지 반환
    }

    public String deleteAllChat() { // 저장된 전체 대화 내역을 모두 삭제하는 메서드
        this.jdbcChatMemoryRepository.findConversationIds().forEach(this.jdbcChatMemoryRepository::deleteByConversationId); // 모든 conversationId 를 조회해서 하나씩 삭제
        return "Delete All Completed "; // 전체 삭제 완료 메시지 반환
    }

    public String chat(String question, String conversationId) { // conversationId 기준으로 대화 메모리를 유지하며 질문하는 메서드
        return chatClient.prompt() // 새 프롬프트 생성 시작
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.") // 시스템 메시지 설정
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId)) // 현재 대화를 구분하기 위한 conversationId 전달
                .user(question) // 사용자 질문 설정
                .call() // 모델 호출 수행
                .content(); // 응답 본문만 추출
    }
}