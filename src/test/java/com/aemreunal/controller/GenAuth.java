package com.aemreunal.controller;

/*
 ***************************
 * Copyright (c) 2014      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * aemreunal@gmail.com     *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

import org.junit.Test;
import org.springframework.security.crypto.codec.Base64;

public class GenAuth {
    @Test
    public void generateAuth() {
        String auth = "adminr:password";
        byte[] encodedAuthorisation = Base64.encode(auth.getBytes());
        System.out.println("Basic " + new String(encodedAuthorisation));
    }
}
