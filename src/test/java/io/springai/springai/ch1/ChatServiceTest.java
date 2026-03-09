package io.springai.springai.ch1;

import io.springai.springai.app.service.ch1.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
@Slf4j
class ChatServiceTest {

    @Autowired
    ChatService chatService;

    @Test
    void contextLoads() {
        String question = "천안에 맛집 알려줘";
        // Flux<String> result = chatService.requestTextStream(question);
        // result.collectList().block().stream().forEach(System.out::println);
        /**
         * 2026-03-09T04:46:44.532+09:00 ERROR 28045 --- [spring-ai] [    Test worker] i.n.r.d.DnsServerAddressStreamProviders  : Unable to load io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider, fallback to system defaults. This may result in incorrect DNS resolutions on MacOS. Check whether you have a dependency on 'io.netty:netty-resolver-dns-native-macos'. Use DEBUG level to see the full stack: java.lang.UnsatisfiedLinkError: failed to load the required native library
         *
         * 천
         * 안
         * 에는
         *  다양한
         *  맛
         * 집
         * 이
         *  많
         * 아
         *  방문
         * 하기
         * 에
         *  좋은
         *  곳
         * 들이
         *  많
         * 습니다
         * .
         *  몇
         */

        String result = chatService.requestText(question);
        log.info("result:{}", result);
        /**
         * 2026-03-09T04:43:57.462+09:00  INFO 27950 --- [spring-ai] [    Test worker] i.springai.springai.ch1.ChatServiceTest  : result:천안에는 다양한 맛집이 있어요. 여기 몇 가지 추천해드릴게요.
         *
         * 1. **두정동 청년다방** - 떡볶이와 튀김을 맛볼 수 있는 곳으로, 다양한 종류의 떡볶이가 인기입니다.
         *
         * 2. **성정동 고기원칙** - 소고기와 돼지고기의 질이 좋아 고기 맛을 제대로 즐길 수 있는 고깃집입니다.
         *
         * 3. **불당동 탐나는 칼국수** - 해산물 육수로 만든 칼국수가 일품인 곳으로, 꼬막 비빔밥도 인기 있습니다.
         *
         * 4. **신부동 연타발** - 삼겹살과 돼지갈비를 맛볼 수 있는 곳으로, 특히 특제 소스가 독특합니다.
         *
         * 5. **두정동 호호불백** - 매운 불고기를 맛볼 수 있는 곳으로, 점심시간에 직장인들에게 인기가 많습니다.
         *
         * 이 외에도 천안에는 다양한 맛집들이 많이 있으니, 개인의 취향에 따라 선택해보세요!
         */
    }
}
