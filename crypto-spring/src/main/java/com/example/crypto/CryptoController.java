package com.example.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class CryptoController {

    @Autowired
    CryptoService cryptoService;

    @GetMapping("/crypto/candlesticks")
    public ResponseEntity<List<CandleStick>> getCandleSticks(){
        return ResponseEntity.status(HttpStatus.OK).body(cryptoService.getAllCandleSticks());
    }
}
