package com.example.demo.bitcoinj;

import com.example.demo.dto.SendCoinsTarget;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// NOTE: https://github.com/bitcoinj/bitcoinj/tree/master/examples/src/main/java/org/bitcoinj/examples


@Component
public class MyWallet {
    private boolean _isReady = false;

    @Autowired
    private WalletAppKit walletAppKit;

    @PostConstruct
    public void start() throws IOException {
        if (networkParameters() == RegTestParams.get()) {
            walletAppKit.connectToLocalHost();
        }

        if (walletAppKit.isChainFileLocked()) {
            throw new RuntimeException("Already running, This application is already running and cannot be started twice.");
        }
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        _isReady = true;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        _isReady = false;
        walletAppKit.stopAsync();
        walletAppKit.awaitTerminated();
    }

    public void sendToAddress(@NotBlank @Size(min = 1) List<SendCoinsTarget> targets) throws InsufficientMoneyException {
        assertIsReady();
        // Parse the address given as the first parameter.
        Transaction tx = new Transaction(networkParameters());

        targets.forEach(
                target -> {
                    tx.addOutput(new TransactionOutput(
                            networkParameters(),
                            tx,
                            Coin.valueOf(target.satoshis),
                            Address.fromString(networkParameters(), target.address)
                    ));
                }
        );

        final SendRequest sendRequest = SendRequest.forTx(tx);
        walletAppKit.wallet().sendCoins(walletAppKit.peerGroup(), sendRequest);
    }

    public PrivatePublicKeyPair createAddress() {
        assertIsReady();

        final Address address = walletAppKit.wallet().freshReceiveAddress();

        return getPrivatePublicKeyPair(address);
    }

    private PrivatePublicKeyPair getPrivatePublicKeyPair(Address address) {
        String publicKeyEncoded;
        if (address instanceof LegacyAddress) {
            publicKeyEncoded = ((LegacyAddress) address).toBase58();
        } else if (address instanceof SegwitAddress) {
            publicKeyEncoded = ((SegwitAddress) address).toBech32();
        } else {
            throw new RuntimeException(
                    "Unsupported created address of type " + address.getClass() +
                            ", this is probably a new address. Check with developer for more information"
            );
        }

        final ECKey keyFromPubKey = walletAppKit.wallet().findKeyFromPubKeyHash(address.getHash(), address.getOutputScriptType());

        final String privateKeyEncoded = keyFromPubKey == null
                ? null
                : keyFromPubKey.getPrivateKeyEncoded(networkParameters()).toBase58();

        return new PrivatePublicKeyPair(publicKeyEncoded, privateKeyEncoded, address.getOutputScriptType());
    }

    private NetworkParameters networkParameters() {
        return walletAppKit.params();
    }

    public void assertIsValidDestinationAddress(String addressField) throws AddressFormatException {
        Address.fromString(networkParameters(), addressField);
    }

    public Map<Wallet.BalanceType, Long> getBalances() {
        assertIsReady();

        return Arrays.stream(Wallet.BalanceType.values())
                .collect(Collectors.toMap(
                        Function.identity(),
                        (balanceType) -> {
                            return walletAppKit.wallet().getBalance(balanceType).getValue();
                        }));
    }

    public Stream<PrivatePublicKeyPair> getAddresses() {
        assertIsReady();

        List<Address> issuedReceiveAddresses = walletAppKit.wallet().getIssuedReceiveAddresses();

        return issuedReceiveAddresses.stream().map(this::getPrivatePublicKeyPair);
    }

    private void assertIsReady() {
        if (!_isReady) {
            throw new BitcoinjNotReadyException();
        }
    }

    public boolean isReady() {
        return this._isReady;
    }
}
