package io.springai.springai.app.service.ch7;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch7_HotelEmbeddingModelService {

  private final VectorStore vectorStore; // 임베딩된 호텔 문서를 저장하고 유사도 검색을 수행할 VectorStore

  List<Document> documents = List.of( // 호텔 관련 샘플 문서 목록
          new Document("호텔 입실 시간은 오후 3시 입니다.", Map.of("section", "regulation", "name", "hotel1")), // hotel1 입실 규정
          new Document("호텔 퇴실 시간은 오전 11시 입니다.", Map.of("section", "regulation", "name", "hotel1")), // hotel1 퇴실 규정
          new Document("호텔 입실 시간은 오후 2시 입니다.", Map.of("section", "regulation", "name", "hotel2")), // hotel2 입실 규정
          new Document("호텔 퇴실 시간은 오전 12시 입니다.", Map.of("section", "regulation", "name", "hotel2")), // hotel2 퇴실 규정
          new Document("호텔 조식 시간은 오전 7시부터 오전 9시까지 입니다.", Map.of("section", "restaurant", "name", "hotel1")), // hotel1 조식 정보
          new Document("호텔 석식 시간은 오후 6시부터 오후 9시까지 입니다.", Map.of("section", "restaurant", "name", "hotel1")), // hotel1 석식 정보
          new Document("호텔 주변 관광지는 설악산 국립공원이 있습니다.", Map.of("section", "additional", "name", "hotel1")), // hotel1 주변 관광지 정보
          new Document("호텔 주변 맛집은 순두부집이 있습니다.", Map.of("section", "additional", "name", "hotel1"))); // hotel1 주변 맛집 정보

  // Constructor
  public Ch7_HotelEmbeddingModelService(VectorStore vectorStore) { // VectorStore 를 주입받는 생성자
    this.vectorStore = vectorStore; // 주입받은 VectorStore 저장
  }

  public String addData() { // 호텔 문서들을 임베딩한 뒤 VectorStore 에 저장하는 메서드
    vectorStore.add(documents); // documents 목록을 임베딩 후 VectorStore 에 추가
    return " Add Completed"; // 저장 완료 메시지 반환
  }

  public String deleteDate() { // hotel1, hotel2 관련 문서를 삭제하는 메서드
    vectorStore.delete("name == 'hotel1' or name == 'hotel2'"); // metadata 의 name 값이 hotel1 또는 hotel2 인 문서 삭제
    return "Delete Completed "; // 삭제 완료 메시지 반환
  }

  public List<Document> similaritySearch(String question) { // 질문과 유사한 호텔 문서를 전체 범위에서 검색하는 메서드
    return vectorStore.similaritySearch(question); // 질문을 임베딩해 가장 유사한 문서 목록 반환
  }

  public List<Document> similaritySearch(String question, String section, String name) { // section 과 hotel 이름으로 필터링해서 유사한 문서를 검색하는 메서드
    return vectorStore.similaritySearch(
            SearchRequest.builder() // 상세 검색 요청 생성 시작
                    .query(question) // 사용자 질문 설정
                    .topK(1) // 상위 1개 결과만 조회
                    .similarityThreshold(0.5) // 유사도 임계값 0.5 이상만 조회
                    .filterExpression("section == '%s' and name == '%s'".formatted(section, name)) // section, name 조건으로 metadata 필터 적용
                    .build()); // SearchRequest 생성 완료
  }

  // filterExpression
  public List<Document> similaritySearch(String question, String director, int year) { // director, year 조건으로 필터링해서 유사한 문서를 검색하는 예시 메서드
    FilterExpressionBuilder b = new FilterExpressionBuilder(); // 필터 표현식 빌더 생성

    return vectorStore.similaritySearch(
            SearchRequest.builder() // 상세 검색 요청 생성 시작
                    .query(question) // 사용자 질문 설정
                    .topK(1) // 상위 1개 결과만 조회
                    .similarityThreshold(0.5) // 유사도 임계값 0.5 이상만 조회
                    .filterExpression(b.and(b.eq("director", director), b.gte("year", year)).build()) // director 일치 && year 이상 조건을 metadata 필터로 적용
                    .build()); // SearchRequest 생성 완료
    //.filterExpression("derector  == '%s' and year >= '%s'".formatted(director,year)).build()); // 문자열로 직접 조건식을 작성하는 예시
  }

}
