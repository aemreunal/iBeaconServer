package com.aemreunal.domain;

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

import com.aemreunal.helper.RestHelper;

public class EntityRemover {

    protected static void removeEntity(String path) {
        path += "?confirm=yes";
        RestHelper.deleteEntityRequest(path);
    }

}
