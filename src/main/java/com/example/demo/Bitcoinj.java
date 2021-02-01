package com.example.demo;

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

// NOTE: https://github.com/bitcoinj/bitcoinj/tree/master/examples/src/main/java/org/bitcoinj/examples

/**
 * ForwardingService demonstrates basic usage of the library. It sits on the network and when it receives coins, simply
 * sends them onwards to an address given on the command line.
 */
public class Bitcoinj {
    public enum NETWORK {
            MAIN_NET,
            TEST_NET,
            REG_NET,
    }

    private WalletAppKit kit;
    private final String filePrefix;
    private final NetworkParameters params;

    public Bitcoinj(NETWORK network) {
        switch (network) {
            case REG_NET:
                params = RegTestParams.get();
                filePrefix = "forwarding-service-regtest";
                break;
            case MAIN_NET:
                params = MainNetParams.get();
                filePrefix = "forwarding-service";
                break;
            case TEST_NET:
                params = TestNet3Params.get();
                filePrefix = "forwarding-service-testnet";
                break;
            default:
                throw new RuntimeException();
        }
    }

    public void main() throws Exception {
        // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
//        BriefLogFormatter.init();

        // Figure out which network we should connect to. Each one gets its own set of files.


        // Start up a basic app using a class that automates some boilerplate.
        kit = new WalletAppKit(params, new File("."), filePrefix);

        if (params == RegTestParams.get()) {
            // Regression test mode is designed for testing and development only, so there's no public network for it.
            // If you pick this mode, you're expected to be running a local "bitcoind -regtest" instance.
            kit.connectToLocalHost();
        }

        // Download the block chain and wait until it's done.
        kit.startAsync();
        kit.awaitRunning();
    }

    public void sendToAddress(String toAddressStr, long satoshis) throws InsufficientMoneyException {
        // Parse the address given as the first parameter.
        final Address toAddress = LegacyAddress.fromBase58(params, "za3ma address");

        System.out.println("Network: " + params.getId());
        System.out.println("Forwarding address: " + toAddress);
        // We want to know when we receive money.
        kit.wallet().createSend(toAddress, Coin.valueOf(satoshis));
        Address sendToAddress = LegacyAddress.fromKey(params, kit.wallet().currentReceiveKey());
        System.out.println("Send coins to: " + sendToAddress);
        System.out.println("Waiting for coins to arrive. Press Ctrl-C to quit.");
    }

    public long getBalanceInStaoshis() {
        return kit.wallet().getBalance().getValue();
    }
}
