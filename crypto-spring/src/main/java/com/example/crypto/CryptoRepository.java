package com.example.crypto;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository {

    public default List<CandleStick> getAllCandleSticks(){
        return null;
    }
}
