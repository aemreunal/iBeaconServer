package com.aemreunal.helper;

/*
 ***************************
 * Copyright (c) 2015      *
 *                         *
 * This code belongs to:   *
 *                         *
 * @author Ahmet Emre Ünal *
 * S001974                 *
 *                         *
 * emre@aemreunal.com      *
 * emre.unal@ozu.edu.tr    *
 *                         *
 * aemreunal.com           *
 ***************************
 */

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;
import com.aemreunal.config.GlobalSettings;

public class ImageStorage {

    /**
     * Saves the given image (in bytes) to the filesystem and returns the name of the
     * saved image file.
     * <p>
     * The method saves images under the home folder of the user, inside the <code>{@value
     * com.aemreunal.config.GlobalSettings#ROOT_STORAGE_FOLDER_DIRECTORY_NAME}</code> root
     * folder, <code>{@value com.aemreunal.config.GlobalSettings#IMAGE_STORAGE_FOLDER_DIRECTORY_NAME}</code>
     * sub folder. If the folder does not exist, it will be created.
     *
     * @param username
     *         The username of whoever is saving the image.
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image.
     * @param existingImageName
     *         If an image of a similar function already exists, the name of that image
     *         file should be given with this parameter. When this parameter is not
     *         <code>null</code>, this file is deleted prior to saving the new one.
     * @param imageAsBytes
     *         The image as a <code>byte</code> array.
     *
     * @return The name of the saved image file.
     */
    public String saveImage(String username, Long projectId, Long regionId, String existingImageName, byte[] imageAsBytes) {
        File imageFile;
        String fileName;
        String filePath = getFilePath(username, projectId, regionId);

        // Delete pre-existing image file
        if (existingImageName != null) {
            if (!deleteImage(username, projectId, regionId, existingImageName)) {
                System.err.println("Unable to delete existing image!");
                return null;
            }
        }

        // Ensure unique file name
        do {
            fileName = UUID.randomUUID().toString();
            imageFile = new File(filePath + fileName);
        } while (imageFile.exists());

        // Check for the existence of the parent folder and create
        // if it doesn't exist
        if (!createParentFolder(imageFile)) {
            System.err.println("Unable to create parent folder!");
            return null;
        }

        // Create the new image file
        if (!createFile(imageFile)) {
            System.err.println("Unable to create file!");
            return null;
        }

        // Write the image bytes to the new file
        if (!writeImageToFile(imageAsBytes, imageFile)) {
            System.err.println("Unable to write image to file!");
            return null;
        }
        return fileName;
    }

    private boolean createParentFolder(File imageFile) {
        if (!imageFile.getParentFile().exists()) {
            // If it doesn't exist, create it
            if (!imageFile.getParentFile().mkdirs()) {
                return false;
            }
        }
        return true;
    }

    private boolean createFile(File imageFile) {
        try {
            if (!imageFile.createNewFile()) {
                System.err.println("Unable to createNewFile()!");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Unable to create file!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean writeImageToFile(byte[] imageAsBytes, File imageFile) {
        try {
            FileOutputStream stream = new FileOutputStream(imageFile);
            stream.write(imageAsBytes);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Loads the specified image file and returns the bytes of the image file in a
     * <code>byte[]</code>.
     *
     * @param username
     *         The username of whoever is loading the image.
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image.
     * @param imageFileName
     *         The name of the image file to be loaded.
     *
     * @return A <code>byte[]</code> if the image file has been successfully loaded and
     * read, <code>null</code> otherwise.
     */
    public byte[] loadImage(String username, Long projectId, Long regionId, String imageFileName) {
        String filePath = getFilePath(username, projectId, regionId);
        File imageFile = new File(filePath + imageFileName);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist!");
            return null;
        }
        // Can safely cast from long to int, as image file will never exceed
        // 2^32 bytes (Which would've used 64 bits of long).
        byte[] imageAsBytes = new byte[(int) imageFile.length()];
        if (!loadImageFromFile(imageFile, imageAsBytes)) {
            System.err.println("Unable to load image file!");
            return null;
        }
        return imageAsBytes;
    }

    private boolean loadImageFromFile(File imageFile, byte[] imageAsBytes) {
        try {
            FileInputStream stream = new FileInputStream(imageFile);
            stream.read(imageAsBytes);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Deletes the specified image file.
     *
     * @param username
     *         The username of whoever is deleting the image.
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image.
     * @param imageFileName
     *         The name of the image file to be deleted.
     *
     * @return <code>true</code> if image file has been successfully deleted,
     * <code>false</code> otherwise.
     */
    public boolean deleteImage(String username, Long projectId, Long regionId, String imageFileName) {
        String filePath = getFilePath(username, projectId, regionId);
        File imageFile = new File(filePath + imageFileName);
        try {
            Files.delete(imageFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String getFilePath(String username, Long projectId, Long regionId) {
        return GlobalSettings.IMAGE_STORAGE_FOLDER_PATH + username + "/" + projectId.toString() + "/" + regionId.toString() + "/";
    }
}
