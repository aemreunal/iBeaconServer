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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.exception.imageStorage.ImageDeleteException;
import com.aemreunal.exception.imageStorage.ImageLoadException;
import com.aemreunal.exception.imageStorage.ImageSaveException;
import com.aemreunal.exception.region.MultipartFileReadException;
import com.aemreunal.exception.region.WrongFileTypeSubmittedException;

public class ImageStorage {

    /**
     * Saves the given image to the filesystem and returns the properties of the saved
     * image file as an {@link ImageProperties ImageProperties} object.
     * <p>
     * The method saves images under the home folder of the user, inside the:<pre>
     * ~/{@value com.aemreunal.config.GlobalSettings#ROOT_STORAGE_FOLDER_DIRECTORY_NAME}/{@value
     * com.aemreunal.config.GlobalSettings#IMAGE_STORAGE_FOLDER_DIRECTORY_NAME}/</pre>
     * folder. The regular region images will be put in a sub-folder structure like:<pre>
     * &lt;project ID&gt;/&lt;region ID&gt;/</pre> under the main
     * storage folder. Inter-region navigation images will be put directly under
     * the:<pre>
     * &lt;user name&gt;/&lt;project ID&gt;/</pre> folder, without any region-specific
     * foldering.
     * <p>
     * If any of the folders above do not exist, they will be created.
     *
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image. When saving an inter-region navigation
     *         connection image, this value should be {@code null}.
     * @param imageMultipartFile
     *         The image as a {@link MultipartFile MultipartFile}.
     *
     * @return The properties of the saved image file as an {@link ImageProperties
     * ImageProperties} object. These properties are: <ul> <li>Image name</li> <li>Image
     * width</li> <li>Image height</li> </ul>
     *
     * @throws MultipartFileReadException
     *         If the {@link MultipartFile Multipart file} can't be read.
     * @throws ImageSaveException
     *         If the image can't be saved.
     * @throws WrongFileTypeSubmittedException
     *         If the submitted {@link MultipartFile Multipart file} is of a wrong type
     *         (e.g. non-image file, unacceptable image type).
     */
    public ImageProperties saveImage(Long projectId, Long regionId, MultipartFile imageMultipartFile)
            throws MultipartFileReadException, ImageSaveException, WrongFileTypeSubmittedException {
        // Verify file is not empty or is not of a wrong type
        verifyImageType(projectId, regionId, imageMultipartFile);

        // Get the file path from the project ID and region ID attributes
        String filePath = getFilePath(projectId, regionId);

        // Ensure unique file name
        File imageFile = getUniqueFile(filePath);

        // Check for the existence of the parent folder and create it
        // if it doesn't exist
        createParentFolder(projectId, regionId, imageFile);

        // Create the new image file
        createFile(projectId, regionId, imageFile);

        // Write the image bytes to the new file
        writeImageToFile(projectId, regionId, imageMultipartFile, imageFile);

        // Read the image properties to get dimensions
        return readImageProperties(projectId, regionId, imageFile);
    }

    /**
     * Loads the specified image file and returns the bytes of the image file in a
     * <code>byte[]</code>.
     *
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image. When saving an inter-region navigation
     *         connection image, this value should be {@code null}.
     * @param imageFileName
     *         The name of the image file to be loaded.
     *
     * @return A <code>byte[]</code> if the image file has been successfully loaded and
     * read, <code>null</code> otherwise.
     *
     * @throws ImageLoadException
     *         If the image can't be loaded.
     */
    public byte[] loadImage(Long projectId, Long regionId, String imageFileName)
            throws ImageLoadException {
        // Get the file path from the project ID and region ID attributes
        String filePath = getFilePath(projectId, regionId);
        // Get the image file
        File imageFile = new File(filePath + imageFileName);
        if (!imageFile.exists()) {
            System.err.println("Image file does not exist!");
            throw new ImageLoadException(projectId, regionId);
        }
        return loadImageFromFile(projectId, regionId, imageFile);
    }

    /**
     * Deletes the specified image file. The method will do nothing if a {@code null}
     * value is provided for {@code imageFileName} parameter.
     *
     * @param projectId
     *         The ID of the project which the region (the image belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the image.
     * @param imageFileName
     *         The name of the image file to be deleted. If a {@code null} value is
     *         provided, the method will do nothing and return.
     *
     * @throws ImageDeleteException
     *         If the image file can't be deleted.
     */
    public void deleteImage(Long projectId, Long regionId, String imageFileName)
            throws ImageDeleteException {
        if (imageFileName == null || imageFileName.equals("")) {
            return;
        }
        // Get the file path from the project ID and region ID attributes
        String filePath = getFilePath(projectId, regionId);
        // Get the image file
        File imageFile = new File(filePath + imageFileName);
        try {
            Files.delete(imageFile.toPath());
        } catch (NoSuchFileException e) {
            GlobalSettings.err("WARNING: Image file for project: "
                                       + projectId + ", region " + regionId + ", file name: "
                                       + imageFileName + " does not exist, nothing to delete!");
        } catch (IOException e) {
            GlobalSettings.err("Unable to delete the image!");
            throw new ImageDeleteException(projectId, regionId);
        }
    }

    private void verifyImageType(Long projectId, Long regionId, MultipartFile imageMultipartFile) throws WrongFileTypeSubmittedException {
        if (imageMultipartFile.isEmpty() || !fileTypeIsImage(imageMultipartFile)) {
            throw new WrongFileTypeSubmittedException(projectId, regionId);
        }
    }

    private boolean fileTypeIsImage(MultipartFile file) {
        String type = file.getContentType();
        return type.equalsIgnoreCase(MediaType.IMAGE_JPEG_VALUE) ||
                type.equalsIgnoreCase(MediaType.IMAGE_PNG_VALUE) ||
                type.equalsIgnoreCase(MediaType.IMAGE_GIF_VALUE);
    }

    private File getUniqueFile(String filePath) {
        File imageFile;
        do {
            String fileName = UUID.randomUUID().toString();
            imageFile = new File(filePath + fileName);
        } while (imageFile.exists());
        return imageFile;
    }

    private void createParentFolder(Long projectId, Long regionId, File imageFile) throws ImageSaveException {
        if (!imageFile.getParentFile().exists()) {
            // If it doesn't exist, create it
            if (!imageFile.getParentFile().mkdirs()) {
                System.err.println("Unable to create parent folders!");
                throw new ImageSaveException(projectId, regionId);
            }
        }
    }

    private void createFile(Long projectId, Long regionId, File imageFile) throws ImageSaveException {
        try {
            if (!imageFile.createNewFile()) {
                System.err.println("Unable to createNewFile()!");
                throw new ImageSaveException(projectId, regionId);
            }
        } catch (IOException e) {
            System.err.println("Unable to create file!");
            throw new ImageSaveException(projectId, regionId);
        }
    }

    private void writeImageToFile(Long projectId, Long regionId, MultipartFile imageMultipartFile, File imageFile) throws ImageSaveException {
        try {
            imageMultipartFile.transferTo(imageFile);
        } catch (IOException e) {
            System.err.println("Unable to write image to file!");
            throw new ImageSaveException(projectId, regionId);
        }
    }

    private ImageProperties readImageProperties(Long projectId, Long regionId, File imageFile)
    throws ImageSaveException {
        try {
            BufferedImage image = ImageIO.read(imageFile);
            return new ImageProperties(imageFile.getName(), image.getWidth(), image.getHeight());
        } catch (IOException e) {
            System.err.println("Unable to read image to get dimensions!");
            throw new ImageSaveException(projectId, regionId);
        }
    }

    private byte[] loadImageFromFile(Long projectId, Long regionId, File imageFile) throws ImageLoadException {
        // Can safely cast from long to int, as image file will never exceed
        // 2^32 bytes (which would've required the use of a 64 bit long).
        byte[] imageAsBytes = new byte[(int) imageFile.length()];
        try {
            FileInputStream stream = new FileInputStream(imageFile);
            stream.read(imageAsBytes);
            stream.close();
        } catch (FileNotFoundException e) {
            System.err.println("File to read from is not found!");
            throw new ImageLoadException(projectId, regionId);
        } catch (IOException e) {
            System.err.println("Unable to read from file!");
            throw new ImageLoadException(projectId, regionId);
        }
        return imageAsBytes;
    }

    private String getFilePath(Long projectId, Long regionId) {
        if (regionId != null) {
            return GlobalSettings.IMAGE_STORAGE_FOLDER_PATH + projectId.toString() + "/" + regionId.toString() + "/";
        } else {
            return GlobalSettings.IMAGE_STORAGE_FOLDER_PATH + projectId.toString() + "/";
        }
    }
}
