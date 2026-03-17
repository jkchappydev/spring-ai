package io.springai.springai.app.service.ch6.tools.card;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccessSystemTools {

    @Tool(description = "사번이 일치하면 출입문을 연다.", returnDirect = true)
    public String open() {
        return "출입문이 열립니다.";
    }
    @Tool(description = "사번이 일치하지 않으면 출문을 열수 없다.", returnDirect = true)
    public String close() {
        return "출입문을 열수 없습니다.";
    }

}