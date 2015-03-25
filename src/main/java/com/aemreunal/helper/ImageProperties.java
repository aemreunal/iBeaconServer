package com.aemreunal.helper;

/*
 * *********************** *
 * Copyright (c) 2015      *
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
 * *********************** *
 */

/**
 * Represents the properties of an image. The represented properties of the image are:
 * <ul> <li>Image name</li> <li>Image width</li> <li>Image height</li> </ul>
 */
public class ImageProperties {
    private final String imageFileName;
    private final int    imageWidth;
    private final int    imageHeight;

    public ImageProperties(String imageFileName, int imageWidth, int imageHeight) {
        this.imageFileName = imageFileName;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
