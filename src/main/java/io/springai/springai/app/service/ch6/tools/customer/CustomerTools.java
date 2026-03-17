package io.springai.springai.app.service.ch6.tools.customer;

import io.springai.springai.app.dto.ch6.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustomerTools {

    CustomerService customerService;

    public CustomerTools(){
        customerService=new CustomerService();
    }

    // returnDirect = true는 다시 LLM에 전송 없이 결과를 바로 전송
    @Tool(description = "특정 ID의 사용자의 정보를 조회. ", returnDirect = true)
    Customer getCustomer(@ToolParam(description = "사용자의 ID", required = true) String id) {
        return customerService.getCustomer(id);
    }
    @Tool(description = "모든 또는 전체 사용자 정보를 조회. ", returnDirect = true)
    List<Customer> getAllCustomer() {
        return customerService.getCustomers();
    }
}