package io.springai.springai.app.service.ch6.tools.shopping;

import io.springai.springai.app.dto.ch6.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
//@RequiredArgsConstructor
public class ShoppingTools {

    ShoppingService shoppingService = new ShoppingService();

    // ToolContext에 직접 입력한 사용자 ID를 이용
    @Tool(description = "특정 ID 사용자의 구매 목록을 조회. ")
    List<Shop> getOrderedByCustomer(ToolContext toolContext) {
        String userId = (String) toolContext.getContext().get("userId");
        return shoppingService.getOrderedByCustomer(userId);
    }

    @Tool(description = "고객이 주로 구매한 카테고리와 구매가격을 기반으로 제품을 검색해줘. ")
    List<Shop> getContents(@ToolParam(description = "카테고리", required = true) String category) {
        return shoppingService.getContents(category);
    }

}