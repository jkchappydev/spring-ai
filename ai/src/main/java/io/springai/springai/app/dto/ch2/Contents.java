package io.springai.springai.app.dto.ch2;

import java.util.List;

public record Contents(
        String location,
        List<Shop> shops
) {

}
