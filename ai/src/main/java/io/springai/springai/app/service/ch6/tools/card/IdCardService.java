package io.springai.springai.app.service.ch6.tools.card;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class IdCardService {

    // 데이터베이스 또는 접근 권한 정보를 조회, 가상의 데이터 셋팅
    public List<String> getIdCardList(){
        return List.of("12345","35679","58473");
    }

    boolean checkIdCardNumber(String idCardNumber){
        return this.getIdCardList().contains(idCardNumber);
    }

}
