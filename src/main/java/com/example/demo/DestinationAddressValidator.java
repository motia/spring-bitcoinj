package com.example.demo;

import com.example.demo.bitcoinj.MyWallet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class DestinationAddressValidator implements
        ConstraintValidator<DestinationAddressConstraint, String> {

    @Autowired
    final MyWallet myWallet;

    public DestinationAddressValidator(MyWallet myWallet) {
        this.myWallet = myWallet;
    }

    @Override
    public void initialize(DestinationAddressConstraint address) {
    }

    @Override
    public boolean isValid(String addressField,
                           ConstraintValidatorContext cxt) {
        if (addressField == null) {
            return false;
        }

        try {
            myWallet.assertIsValidDestinationAddress(addressField);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}