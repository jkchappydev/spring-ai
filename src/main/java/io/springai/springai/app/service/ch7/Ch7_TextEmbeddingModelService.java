package io.springai.springai.app.service.ch7;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch7_TextEmbeddingModelService {

    private final VectorStore vectorStore; // 임베딩된 문서를 저장하고 유사도 검색을 수행할 VectorStore

    List<Document> documents = List.of( // VectorStore 에 저장할 샘플 문서 목록
            new Document("출근 시간은 9시 입니다.", Map.of("key", "regulation")), // 규정 관련 문서 1
            new Document("퇴근 시간은 6시 입니다.", Map.of("key", "regulation")), // 규정 관련 문서 2
            new Document("야근은 없습니다.", Map.of("key", "regulation"))); // 규정 관련 문서 3

    // Constructor
    public Ch7_TextEmbeddingModelService(VectorStore vectorStore) { // VectorStore 를 주입받는 생성자
        this.vectorStore = vectorStore; // 주입받은 VectorStore 저장
    }

    // Text Embbeding 후 Vectore Store에 저장
    // Text Embbeding 시 OpenAI Embedding Models(text-embedding-ada-002)을 기본으로 사용
    public String addData() { // 문서들을 임베딩한 뒤 VectorStore 에 저장하는 메서드
        vectorStore.add(documents); // documents 목록을 임베딩 후 VectorStore 에 추가
        return " Add Completed"; // 저장 완료 메시지 반환
    }

    public String deleteDate() { // key 가 regulation 인 문서들을 삭제하는 메서드
        vectorStore.delete("key == 'regulation'"); // metadata 의 key 값이 regulation 인 문서 삭제
        return "Delete Completed "; // 삭제 완료 메시지 반환
    }

    public List<Document> similaritySearch(String question) { // 질문과 의미적으로 유사한 문서를 검색하는 메서드
        return vectorStore.similaritySearch(question); // 질문을 임베딩해 가장 유사한 문서 목록 반환
    }

}