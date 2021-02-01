package com.example.demo.bitcoinj;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BitcoinjNotReadyException extends ResponseStatusException {
    public BitcoinjNotReadyException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, "Wallet Kit is not ready");
    }
}
