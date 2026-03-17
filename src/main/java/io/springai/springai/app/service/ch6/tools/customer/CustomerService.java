package io.springai.springai.app.service.ch6.tools.customer;

import io.springai.springai.app.dto.ch6.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {
    public Customer getCustomer(String id){
        // 데이터베이스 정보 조회
        return new Customer(id,"James",30);
    }
    public List<Customer> getCustomers(){
        // 데이터베이스 정보 조회
        return List.of(
                new Customer("id01","James1",10),
                new Customer("id02","James2",20),
                new Customer("id03","James3",30));
    }

}
