package io.springai.springai.app.service.ch6.exception;

import org.springframework.ai.tool.execution.DefaultToolExecutionExceptionProcessor;
import org.springframework.ai.tool.execution.ToolExecutionExceptionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Tool 에서 빌생 하는 Exception 처리
// Tool 예외 처리 규칙
// RuntimeException 만 true / false 설정에 따라 처리 방식이 달라진다
// - false: 오류 메시지로 가공해서 LLM 에 다시 전달 가능
// - true : 예외를 바로 throw

// IOException 같은 checked exception 이나 Error 는
// true / false 설정과 관계없이 항상 바로 throw 된다
// 즉, 이런 예외는 LLM 이 후처리하지 않고 스프링 예외 흐름으로 빠진다
@Configuration
public class ToolExceptionHandler {
    @Bean
    ToolExecutionExceptionProcessor toolExecutionExceptionProcessor() {
        return new DefaultToolExecutionExceptionProcessor(false);
    }
}
