package io.springai.springai.controller;

import io.springai.springai.app.dto.ch2.Contents;
import io.springai.springai.app.dto.ch2.Question;
import io.springai.springai.app.dto.ch2.Shop;
import io.springai.springai.app.service.ch2.Ch2_PromptTemplateResourceService;
import io.springai.springai.app.service.ch2.Ch2_StructuredOutputConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ch2")
@Slf4j
@RequiredArgsConstructor
// Chapter 2. Prompt Template Controller
public class Ch2Controller {

    // 1. Prompt Template
    final Ch2_PromptTemplateResourceService ch2PromptTemplateResourceService;
    // 2. ListOutputConverter
    // 3. MapOutputConverter
    // 4. BeanOutputConverter
    // 5. ParameterizedType
    final Ch2_StructuredOutputConverterService ch2StructuredOutputConverterService;

    // 1. Prompt Template
    @RequestMapping("/prompt-template")
    public String promptTemplate(Question question) {
        log.info(question.toString());
        // return ch2PromptTemplateResourceService.promptTemplate1(question);
        // return ch2PromptTemplateResourceService.promptTemplate2(question);
        return ch2PromptTemplateResourceService.promptTemplate3(question);
        // return ch2PromptTemplateResourceService.promptTemplate4(question);
        // return ch2PromptTemplateResourceService.promptTemplate5(question);
    }

    // 2. ListOutputConverter
    @RequestMapping("/list-output")
    public List<String> listOutput(Question question) {
        log.info(question.toString());
        return ch2StructuredOutputConverterService.listOutputConverter(question);
    }

    // 3. MapOutputConverter
    @RequestMapping("/map-output")
    public Map<String, Object> mapOutput(Question question) {
        log.info(question.toString());
        return ch2StructuredOutputConverterService.mapOutputConverter(question);
    }

    // 4. BeanOutputConverter
    @RequestMapping("/bean-output")
    public Contents beanOutput(Question question) {
        log.info(question.toString());
        return ch2StructuredOutputConverterService.beanOutputConverter(question);
    }

    // 5. ParameterizedType
    @RequestMapping("/parameterized-type-reference")
    public List<Shop> parameterizedTypeReference(Question question) {
        log.info(question.toString());
        return ch2StructuredOutputConverterService.parameterizedTypeReference(question);
    }

}
