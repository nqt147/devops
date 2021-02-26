package com.vn.smartpay.alertmonitor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.EXPECTATION_FAILED)
public class AlertException extends RuntimeException {
    public AlertException(String msg) {
        super(msg);
    }
}
