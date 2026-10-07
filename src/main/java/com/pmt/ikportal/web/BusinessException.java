package com.pmt.ikportal.web;

/** Kullaniciya dogrudan gosterilebilecek is kurali hatasi. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
