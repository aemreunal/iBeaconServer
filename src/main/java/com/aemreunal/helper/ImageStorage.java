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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.exception.region.MapImageDeleteException;
import com.aemreunal.exception.region.MapImageLoadException;
import com.aemreunal.exception.region.MapImageSaveException;
import com.aemreunal.exception.region.MultipartFileReadException;

public class ImageStorage {

    /**
     * Saves the given image to the filesystem and returns the name of the saved image
     * file.
     * <p>
     * The method saves images under the home folder of the user, inside the <code>{@value
     * com.aemreunal.config.GlobalSettings#ROOT_STORAGE_FOLDER_DIRECTORY_NAME}</code>
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
     * @param imageMultipartFile
     *         The image as a {@link org.springframework.web.multipart.MultipartFile
     *         MultipartFile}.
     *
     * @return The properties of the saved image file as a {@code String[]} object. These
     * properties, along with their index in the returned {@code String[]} object, are:
     * <ul> <li>0: Image name</li> <li>1: Image width</li> <li>2: Image height</li> </ul>
     */
    public String[] saveImage(String username, Long projectId, Long regionId, String existingImageName, MultipartFile imageMultipartFile) throws MultipartFileReadException, MapImageDeleteException, MapImageSaveException {
        File imageFile;
        String fileName;
        String filePath = getFilePath(username, projectId, regionId);
        String[] imageProperties = new String[3];

        // Delete pre-existing image file
        deleteImage(username, projectId, regionId, existingImageName);

        // Ensure unique file name
        do {
            fileName = UUID.randomUUID().toString();
            imageFile = new File(filePath + fileName);
        } while (imageFile.exists());

        // Check for the existence of the parent folder and create
        // if it doesn't exist
        createParentFolder(projectId, regionId, imageFile);

        // Create the new image file
        createFile(projectId, regionId, imageFile);

        // Write the image bytes to the new file
        writeImageToFile(projectId, regionId, imageMultipartFile, imageFile);

        // Read the image properties to get dimensions
        readImageProperties(projectId, regionId, imageFile, imageProperties);

        return imageProperties;
    }

    private void createParentFolder(Long projectId, Long regionId, File imageFile) throws MapImageSaveException {
        if (!imageFile.getParentFile().exists()) {
            // If it doesn't exist, create it
            if (!imageFile.getParentFile().mkdirs()) {
                System.err.println("Unable to create parent folders!");
                throw new MapImageSaveException(projectId, regionId);
            }
        }
    }

    private void createFile(Long projectId, Long regionId, File imageFile) throws MapImageSaveException {
        try {
            if (!imageFile.createNewFile()) {
                System.err.println("Unable to createNewFile()!");
                throw new MapImageSaveException(projectId, regionId);
            }
        } catch (IOException e) {
            System.err.println("Unable to create file!");
            throw new MapImageSaveException(projectId, regionId);
        }
    }

    private void writeImageToFile(Long projectId, Long regionId, MultipartFile imageMultipartFile, File imageFile) throws MapImageSaveException {
        try {
            imageMultipartFile.transferTo(imageFile);
        } catch (IOException e) {
            System.err.println("Unable to write image to file!");
            throw new MapImageSaveException(projectId, regionId);
        }
    }

    private void readImageProperties(Long projectId, Long regionId, File imageFile, String[] imageProperties)
    throws MapImageSaveException {
        imageProperties[0] = imageFile.getName();
        try {
            BufferedImage image = ImageIO.read(imageFile);
            imageProperties[1] = String.valueOf(image.getWidth());
            imageProperties[2] = String.valueOf(image.getHeight());
        } catch (IOException e) {
            System.err.println("Unable to read image to get dimensions!");
            throw new MapImageSaveException(projectId, regionId);
        }
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
    public byte[] loadImage(String username, Long projectId, Long regionId, String imageFileName) throws MapImageLoadException {
        String filePath = getFilePath(username, projectId, regionId);
        File imageFile = new File(filePath + imageFileName);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist!");
            throw new MapImageLoadException(projectId, regionId);
        }
        return loadImageFromFile(projectId, regionId, imageFile);
    }

    private byte[] loadImageFromFile(Long projectId, Long regionId, File imageFile) throws MapImageLoadException {
        // Can safely cast from long to int, as image file will never exceed
        // 2^32 bytes (Which would've used 64 bits of long).
        byte[] imageAsBytes = new byte[(int) imageFile.length()];
        try {
            FileInputStream stream = new FileInputStream(imageFile);
            stream.read(imageAsBytes);
            stream.close();
        } catch (FileNotFoundException e) {
            System.err.println("File to read from is not found!");
            throw new MapImageLoadException(projectId, regionId);
        } catch (IOException e) {
            System.err.println("Unable to read from file!");
            throw new MapImageLoadException(projectId, regionId);
        }
        return imageAsBytes;
    }

    /**
     * Deletes the specified image file. The method will do nothing if a {@code null}
     * value is provided for {@code imageFileName} parameter.
     *
     * @param username
     *         The username of whoever is deleting the image.
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image.
     * @param imageFileName
     *         The name of the image file to be deleted. If a {@code null} value is
     *         provided, the method will do nothing and return.
     */
    public void deleteImage(String username, Long projectId, Long regionId, String imageFileName) throws MapImageDeleteException {
        if (imageFileName == null || imageFileName.equals("")) {
            return;
        }
        String filePath = getFilePath(username, projectId, regionId);
        File imageFile = new File(filePath + imageFileName);
        try {
            Files.delete(imageFile.toPath());
        } catch (NoSuchFileException e) {
            System.err.println("WARNING: Image file for user: " + username + ", project: "
                                       + projectId + ", region " + regionId + ", file name: "
                                       + imageFileName + " does not exist, nothing to delete!");
        } catch (IOException e) {
            System.err.println("Unable to delete the image!");
            throw new MapImageDeleteException(projectId, regionId);
        }
    }

    private String getFilePath(String username, Long projectId, Long regionId) {
        return GlobalSettings.IMAGE_STORAGE_FOLDER_PATH + username + "/" + projectId.toString() + "/" + regionId.toString() + "/";
    }
}
