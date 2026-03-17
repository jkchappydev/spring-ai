package io.springai.springai.app.service.ch6.tools.shopping;

import io.springai.springai.app.dto.ch6.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ShoppingService {
    public List<Shop> getOrderedByCustomer(String id){
        List<Shop> shops = null;
        // 데이터베이스 정보 조회
        if(id.equals("id01")){
            shops = List.of(
                    new Shop(100,"청반바지",50000,"바지", "빨강"),
                    new Shop(101,"기모바지",20000,"바지", "주황"),
                    new Shop(102,"청바지",40000,"바지", "노랑"),
                    new Shop(103,"흰바지",30000,"바지", "빨강"),
                    new Shop(104,"여름바지",40000,"바지", "주홍")
            );

        }
        if(id.equals("id02")){
            shops = List.of(
                    new Shop(200,"후드티",10000,"상의", "검정"),
                    new Shop(201,"반팔티",20000,"상의", "회색"),
                    new Shop(202,"긴팔티",10000,"상의","흰색"),
                    new Shop(203,"기모티",15000,"상의", "Gray"),
                    new Shop(204,"셔츠",10000,"상의","Black")
            );
        }
        return shops;
    }
    public List<Shop> getContents(String category){
        List<Shop> shops = null;
        // 데이터베이스 정보 조회
        if(category.equals("바지")){
            shops = List.of(
                    new Shop(105,"청반바지1",100000,"바지", "빨강"),
                    new Shop(106,"기모바지2",200000,"바지", "주황"),
                    new Shop(107,"청바지3",40000,"바지", "노랑"),
                    new Shop(108,"흰바지4",30000,"바지", "빨강"),
                    new Shop(109,"여름바지5",40000,"바지", "주홍")
            );

        }
        if(category.equals("상의")){
            shops = List.of(
                    new Shop(205,"후드티1",100000,"상의", "검정"),
                    new Shop(206,"반팔티2",20000,"상의", "회색"),
                    new Shop(207,"긴팔티3",10000,"상의","흰색"),
                    new Shop(208,"기모티4",150000,"상의", "Gray"),
                    new Shop(209,"셔츠5",10000,"상의","Black")
            );
        }
        return shops;
    }

}
