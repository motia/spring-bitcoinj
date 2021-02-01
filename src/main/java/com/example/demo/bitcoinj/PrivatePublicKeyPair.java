package com.example.demo.bitcoinj;

import org.bitcoinj.script.Script;

public class PrivatePublicKeyPair {
    final public String privateKey;
    final public String publicKey;
    final public String outputScriptType;

    PrivatePublicKeyPair(
            String publicKey,
            String privateKey,
            Script.ScriptType outputScriptType
    ) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.outputScriptType = outputScriptType.toString();
    }
}
