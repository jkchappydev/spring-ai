package io.springai.springai.app.service.ch7;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch7_ChatPgvectorService {

    private final ChatClient chatClient; // PgVector 기반 대화 메모리 Advisor 가 적용된 ChatClient

    private final PgVectorStore pgVectorStore; // PostgreSQL + pgvector 에 대화 메모리를 저장할 VectorStore
    private final JdbcTemplate jdbcTemplate; // 직접 SQL 을 실행하기 위한 JdbcTemplate

    // Constructor
    public Ch7_ChatPgvectorService(ChatClient.Builder chatClientBuilder,
                                   JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) { // ChatClient.Builder, JdbcTemplate, EmbeddingModel 을 주입받는 생성자
        this.jdbcTemplate = jdbcTemplate; // 주입받은 JdbcTemplate 저장

        this.pgVectorStore = PgVectorStore.builder(this.jdbcTemplate, embeddingModel) // PgVectorStore 생성 시작
                .initializeSchema(false) // 스키마 자동 생성 비활성화, 테이블은 직접 준비해야 함
                .schemaName("public") // 사용할 DB 스키마 이름 지정
                // 반드시 Pgvector에 테이블 생성
                // /resources/schema 폴도에 있는 sql문이 서버 실행 시 기본적으로 테이블 생성
                .vectorTableName("chat_pgvector") // 벡터 데이터를 저장할 테이블 이름 지정
                .build(); // PgVectorStore 생성 완료

        this.chatClient = chatClientBuilder
                .defaultAdvisors(VectorStoreChatMemoryAdvisor.builder(this.pgVectorStore).build()) // PgVectorStore 기반 대화 메모리 Advisor 등록
                .build(); // ChatClient 생성
    }

    public String deleteChat(String conversationId) { // 특정 conversationId 의 대화 내역만 삭제하는 메서드
        this.pgVectorStore.delete("conversationId == '%s'".formatted(conversationId)); // conversationId 가 일치하는 대화 메모리 삭제
        return "Delete Completed "; // 삭제 완료 메시지 반환
    }

    public String deleteAllChat() { // 전체 대화 내역을 모두 삭제하는 메서드
        this.jdbcTemplate.execute("delete from chat_pgvector"); // chat_pgvector 테이블의 전체 데이터 삭제
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