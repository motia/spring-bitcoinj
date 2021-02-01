package com.example.demo;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

public class BitcoinjFactory {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Bitcoinj createBitcoinj(Bitcoinj.NETWORK network) {
        return new Bitcoinj(network);
    }
}
