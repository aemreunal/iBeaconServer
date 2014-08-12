package com.aemreunal.helper.handler;

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

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

public class PrintHandler implements ResultHandler {
    @Override
    public void handle(MvcResult result) throws Exception {
        System.out.println(result.getResponse().getContentAsString());
    }
}
