package com.example.demo.bitcoinj;


import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class Config {

    @Value("${bitcoin.network:testnet}")
    private String network;

    @Value("${bitcoin.file-prefix:wallet}")
    private String filePrefix;

    @Value("${bitcoin.file-location:storage}")
    private String btcFileLocation;

    public Config() {
        BriefLogFormatter.init();
    }

    private NetworkParameters networkParameters() {
        if(network.equals("testnet")) {
            return TestNet3Params.get();
        } else if(network.equals("regtest")) {
            return RegTestParams.get();
        } else if (network.equals("mainnet")){
            return MainNetParams.get();
        } else {
            throw new RuntimeException("Invalid network " + network);
        }
    }

    @Bean
    public WalletAppKit walletAppKit() {
        return new WalletAppKit(networkParameters(), new File(btcFileLocation), filePrefix + "-" + network) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }
            }
        };
    }
}

