package com.example.demo;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.InsufficientMoneyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class Controller {
    private final Bitcoinj bitcoinj;

    @Autowired
    public Controller(Bitcoinj userRepository) {
        this.bitcoinj = userRepository;
    }

    @RequestMapping("/balance")
    public ResponseEntity<Map<String, Object>> balance() {
        final long balance = bitcoinj.getBalanceInStaoshis();

        return ResponseEntity.status(HttpStatus.OK).body(
                Collections.singletonMap("balance", balance)
        );
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> send(
            @RequestBody SendRequest sendRequest
    ) {
        try {
            bitcoinj.sendToAddress(sendRequest.address, sendRequest.satoshis);
        } catch (InsufficientMoneyException insufficientMoneyException) {
            // TODO:
            throw new RuntimeException();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap());
    }

    private static final  class SendRequest {
        private String address;
        private long satoshis;
    }
}
