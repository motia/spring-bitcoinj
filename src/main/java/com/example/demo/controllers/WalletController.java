package com.example.demo.controllers;

import com.example.demo.DestinationAddressConstraint;
import com.example.demo.bitcoinj.MyWallet;
import com.example.demo.bitcoinj.PrivatePublicKeyPair;
import com.example.demo.dto.SendCoinsRequest;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.wallet.Wallet;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class WalletController {
    private final MyWallet myWallet;

    @Autowired
    public WalletController(MyWallet myWallet) {
        this.myWallet = myWallet;
    }

    @RequestMapping(value = "/wallet/send", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> send(
            @Valid @RequestBody SendCoinsRequest sendRequest
    ) {
        try {
            myWallet.sendToAddress(sendRequest.targets);
        } catch (InsufficientMoneyException insufficientMoneyException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Collections.singletonMap("message", "No enough balance")
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.emptyMap());
    }

    @RequestMapping("/wallet/balances")
    public ResponseEntity<Map<Wallet.BalanceType, Long>> balance() {
        final Map<Wallet.BalanceType, Long> balances = myWallet.getBalances();

        return ResponseEntity.status(HttpStatus.OK).body(balances);
    }

    @RequestMapping("/wallet/addresses")
    public ResponseEntity<List<HashMap<String, String>>> getAddresses(
            @RequestParam(required = false, defaultValue = "0") String withPrivate
    ) {
        Stream<PrivatePublicKeyPair> addresses = myWallet.getAddresses();

        final AddressSerializer serializer = new AddressSerializer(!withPrivate.equals("0"));

        List<HashMap<String, String>> body = addresses.map(serializer::serializeAddress).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    @RequestMapping(value = "/wallet/addresses", method = RequestMethod.POST)
    public ResponseEntity<Map<String, String>> createAddress() {
        PrivatePublicKeyPair address = myWallet.createAddress();
        final AddressSerializer serializer = new AddressSerializer(true);
        HashMap<String, String> map = serializer.serializeAddress(address);

        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }

    private static class AddressSerializer {
        private final boolean withPrivateKey;

        private AddressSerializer() {
            this(false);
        }

        private AddressSerializer(boolean withPrivateKey) {
            this.withPrivateKey = withPrivateKey;
        }

        private HashMap<String, String> serializeAddress(PrivatePublicKeyPair address) {
            HashMap<String, String> map = new HashMap<>();
            if (address == null) {
                return null;
            }
            if (withPrivateKey) {
                map.put("private", address.privateKey);
            }
            map.put("public", address.publicKey);
            map.put("scriptType", address.outputScriptType);
            map.put("withPrivate", withPrivateKey ? "1" : "0");

            return map;
        }
    }
}
