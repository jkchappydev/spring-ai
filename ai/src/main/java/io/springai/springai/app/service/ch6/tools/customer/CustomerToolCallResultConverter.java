package io.springai.springai.app.service.ch6.tools.customer;

import io.springai.springai.app.dto.ch6.Customer;
import org.springframework.ai.tool.execution.ToolCallResultConverter;

import java.lang.reflect.Type;
import java.util.List;

public class CustomerToolCallResultConverter implements ToolCallResultConverter {
    @Override
    public String convert(Object result, Type returnType) {
        if (result instanceof Customer customer) {
            return String.format(
                    "사용자 이름은 %s, 나이는 %s "
                    , customer.name(), customer.age());
        }
        if (result instanceof List) {
            List<Customer> customerList = (List<Customer>) result;

            StringBuilder sb = new StringBuilder();
            customerList.forEach(customer -> {sb.append(String.format(
                    "사용자 이름은 %s, 나이는 %s \n"
                    , customer.name(), customer.age()));});
            return sb.toString();
        }
        return "";
    }
}
