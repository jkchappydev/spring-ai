package io.springai.springai.app.service.ch8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service // 스프링이 관리하는 서비스 컴포넌트로 등록
@Slf4j // log 객체 자동 생성
public class Ch8_EtlPipelineService {

    private final VectorStore vectorStore; // 추출/분할된 문서를 저장할 VectorStore

    // Constructor
    public Ch8_EtlPipelineService(VectorStore vectorStore) { // VectorStore 를 주입받는 생성자
        this.vectorStore = vectorStore; // 주입받은 VectorStore 저장
    }

    public String clearVectorStore(String type) { // 특정 type 에 해당하는 벡터 데이터만 삭제하는 메서드
        vectorStore.delete("type == '%s'".formatted(type)); // metadata 의 type 값이 일치하는 문서 삭제
        return "cleared: " + type; // 삭제 완료 메시지 반환
    }

    public String addVectorStore(String type, MultipartFile attach) throws IOException { // 업로드 파일을 ETL 처리 후 VectorStore 에 저장하는 메서드
        List<Document> documents = textExtraction(attach, Objects.requireNonNull(attach.getContentType())); // 파일의 contentType 에 맞게 Document 추출

        if (documents == null) { // 지원하지 않는 파일 형식이거나 문서 추출 실패 시
            return "파일을 입력 하세요"; // 안내 메시지 반환
        }

        log.info("생성된 Document 수: {} 개", documents.size()); // 추출된 원본 Document 개수 로그 출력

        for (Document doc : documents) { // 각 Document 에 공통 metadata 추가
            doc.getMetadata().put("type", type); // 문서 분류용 type 저장
            doc.getMetadata().put("name", attach.getOriginalFilename()); // 원본 파일명 저장
        }

        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(); // 문서를 토큰 단위로 분할할 Splitter 생성
        List<Document> transformedDocuments = tokenTextSplitter.apply(documents); // 원본 Document 를 작은 청크들로 분할

        log.info("Split 된 Document 수: {} 개", transformedDocuments.size()); // 분할된 Document 개수 로그 출력

        vectorStore.add(transformedDocuments); // 분할된 Document 를 임베딩 후 VectorStore 에 저장

        return "ETL완료"; // ETL 처리 완료 메시지 반환
    }

    private List<Document> textExtraction(MultipartFile attach, String contentType) throws IOException { // 업로드 파일을 contentType 에 따라 Document 로 변환하는 메서드

        Resource resource = new ByteArrayResource(attach.getBytes()); // 업로드 파일 바이트를 Resource 로 변환
        List<Document> documents = null; // 추출 결과를 담을 Document 목록

        switch (contentType) { // 파일 contentType 에 따라 적절한 Reader 선택
            case "text/plain":
                documents = new TextReader(resource).read(); // txt 파일을 읽어서 Document 생성
                break;
            case "application/pdf":
                documents = new PagePdfDocumentReader(resource).read(); // pdf 파일을 페이지 단위로 읽어서 Document 생성
                break;
            case "wordprocessingml":
                documents = new TikaDocumentReader(resource).read(); // 문서 파일을 Tika 로 읽어서 Document 생성
                break;
            default:
                break; // 지원하지 않는 파일 형식이면 null 유지
        }

        return documents; // 추출된 Document 목록 반환
    }
}