package io.springai.springai.app.service.ch6.tools.card;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@Slf4j
//@RequiredArgsConstructor
public class IdCardTools {

    IdCardService idCardService;

    public IdCardTools(){
        this.idCardService = new IdCardService();
    }

    // @ToolParam을 이용해 사진에서 분석한 사번 정보를 Argument로 입력
    @Tool(description = "직원의 사번과 모든 직원 사번과 비교한다.")
    boolean getCardList(@ToolParam(description = "사번") String idCardNumber) {
        return idCardService.checkIdCardNumber(idCardNumber);
    }

}