package io.springai.springai.app.service.ch1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class Ch1_ChatClientService {

    // ChatClient 는 Spring AI 에서 LLM 과 대화할 때 사용하는
    // 좀 더 고수준의 API 이다.
    // 내부적으로 prompt 생성, system/user 메시지 구성, 호출 흐름을
    // 더 간결하게 처리할 수 있다.
    private final ChatClient chatClient;

    // ChatClient.Builder 를 주입받아 ChatClient 객체를 생성한다.
    public Ch1_ChatClientService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // 기본 단건 채팅 요청
    // 사용자 질문 1개를 전달하고, 최종 응답 문자열만 반환한다.
    public String chat(String question) {
        return chatClient.prompt()
                // 모델에게 응답 스타일을 지시하는 system 프롬프트
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.")
                // 실제 사용자 입력
                .user(question)
                // 동기 호출
                .call()
                // 응답 본문 텍스트만 추출
                .content();
    }

    // 스트리밍 채팅 요청
    // 응답을 한 번에 받지 않고, 생성되는 텍스트 조각 단위로 반환한다.
    public Flux<String> chatStream(String question) {
        return chatClient.prompt()
                // 응답 스타일 지정
                .system("질문에 대한 답변을 한국어로 친절하게 답변해야 합니다.")
                // 사용자 질문
                .user(question)
                // 스트리밍 호출
                .stream()
                // 응답 chunk 텍스트만 추출
                .content();
    }

    // 단계별 사고 유도 예시
    // 질문 뒤에 "단계별로 해결해 봅시다"와 예시를 함께 붙여서
    // 모델이 순서대로 설명하도록 유도한다.
    public Flux<String> chatChainOfThought(String question) {
        String userText = """
                %s
                위의 질문을 단계별로 해결해 봅시다.
     
                [예시]
                질문: 
                연비가 1리터에 10Km인 자동차를 가지고 
                서울에서 부산까지 왕복 할때 사용되는 총 소요 비용과 총 소요 시간은 얼마 일까요
                서울에서 부산까지의 거리는 약 500km이고, 평균 속도가 시속 100km라고 가정합니다.
                현재 기름의 가격은 1리터에 1500원 입니다.
                편도 톨게이트 비용은 3만원 입니다.
     
                답변:
                1단계: "서울에서 부산까지 시속 100km의 속도로 이동하는 데 걸리는 시간을 계산해 보겠습니다. 시간은 약 5시간 입니다."
                2단계: "1리터에 10km를 운행 할 수 있으니 500km 운행에 필요한 기름은 50리터 입니다."
                3단계: "50리터를 금액을 환산 하면 75000원 입니다."
                4단계: "따라서 총 운행 시간은 왕복 9시이며 소요되는 기름의 양은 100리터 입니다."
                5단계: "서울에서 부산까지 왕복 하는데 사용되는 총 비용은 왕복 기름값 150000원과 톨게이트 비용 60000원을 합친 210000원 이며 소요 시간은 총 10시간 입니다." 
                """.formatted(question);

        return chatClient.prompt()
                // 예시를 포함한 사용자 프롬프트 전달
                .user(userText)
                // 스트리밍 응답
                .stream()
                // 텍스트 chunk 반환
                .content();
    }

    // Few-shot prompting 예시
    // 모델에게 출력 형식(JSON)과 예시를 먼저 보여준 뒤,
    // 비슷한 형식으로 새 요청에 답하도록 유도한다.
    public String chatFewShot(String question) {

        String userText = """
                사용자가 요청한 명소 또는 맛집 정보를 JSON 형식으로 만들어 줍니다.
                아래 규칙을 반드시 지켜주세요:
                1. 요청한 정보에 대해 10개의 정보를 조회 합니다.
                2. 맛집인 경우 메뉴는 5개이상 알려줘
                2. 응답은 특수문자 형식 없이, JSON 문자열로만 반환해야 합니다.
        
                예시1:
                서울 종로에 맞집 정보 알려줘
                JSON 응답:
                [
                {
                  "name": "옥순당",
                  "menu": ["감자탕", "김치찌개",...],
                  "address": "서울시 중구 371번지",
                   "lat": 37.4985,
                   "lng": 127.0300
                },
                {
                  "name": "옥순당",
                  "menu": ["감자탕", "김치찌개",....],
                  "address": "서울시 중구 371번지",
                   "lat": 37.4985,
                   "lng": 127.0300
                },...
                ]
                예시2:
                서울 종로에 숙박업소 정보 알려줘
                JSON 응답:
                {
                  "name": "옥순장",
                  "address": "서울시 중구 371번지",
                   "lat": 37.4985,
                   "lng": 127.0300
                }

                고객 주문: %s""".formatted(question);

        return chatClient.prompt()
                // 예시 + 규칙 + 실제 사용자 요청 전달
                .user(userText)
                // 동기 호출
                .call()
                // 결과 텍스트 반환
                .content();
    }
}