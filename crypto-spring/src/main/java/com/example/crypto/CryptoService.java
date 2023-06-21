package com.example.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CryptoService {

    @Autowired
    CryptoRepository cryptoRepository;

    public List<CandleStick> getAllCandleSticks(){
        return cryptoRepository.getAllCandleSticks();
    }
}
