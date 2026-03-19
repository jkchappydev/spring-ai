package io.springai.springai.app.service.ch2;

import io.springai.springai.app.dto.ch2.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class Ch2_PromptTemplateResourceService {

    private final ChatClient chatClient;

    // src/main/resources 폴더에 prompts 폴더 생성 후 아래 prompt template 파일을 생성
    @Value("classpath:prompts/system-message-prompt-template.st")
    private Resource systemResource;
    @Value("classpath:prompts/user-message-prompt-template.st")
    private Resource userResource;

    // Constructor
    public Ch2_PromptTemplateResourceService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String promptTemplate1(Question question) {
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource);
        Prompt prompt = userQuestionTemplate.create(Map.of("location", question.location(), "content", question.content()));

        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }

    public String promptTemplate2(Question question) {
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource);
        String userQuestion = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content()));

        return chatClient.prompt()
                .user(userQuestion)
                .call()
                .content();
    }
    public String promptTemplate3(Question question)  {

        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource);
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource);
        String userQuestion  = userQuestionTemplate.render(Map.of("location", question.location(), "content", question.content()));
        String systemMessage  = systemTemplate.render(Map.of("language", question.language()));

        return chatClient.prompt()
                .system(systemMessage)
                .user(userQuestion)
                .call()
                .content();
    }
    public String promptTemplate4(Question question) {
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource);
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource);

        Message userQuestionMessage  = userQuestionTemplate.createMessage(Map.of("location", question.location()));
        Message systemMessage  = systemTemplate.createMessage(Map.of("language", question.language()));
        Prompt prompt = new Prompt(List.of(userQuestionMessage,systemMessage));

        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    public String promptTemplate5(Question question) {
        SystemPromptTemplate userQuestionTemplate = new SystemPromptTemplate(userResource);
        SystemPromptTemplate systemTemplate = new SystemPromptTemplate(systemResource);

        Message userQuestionMessage  = userQuestionTemplate.createMessage(Map.of("location", question.location(), "content", question.content()));
        Message systemMessage  = systemTemplate.createMessage(Map.of("language", question.language()));

        return chatClient.prompt()
                .messages(systemMessage, userQuestionMessage)
                .call()
                .content();
    }

}
