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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import com.aemreunal.config.GlobalSettings;
import com.aemreunal.exception.textStorage.TextDeleteException;
import com.aemreunal.exception.textStorage.TextLoadException;
import com.aemreunal.exception.textStorage.TextSaveException;

public class TextStorage {

    /**
     * Saves the given text to the filesystem and returns the name of the saved text file
     * as a {@link String}.
     * <p>
     * The method saves texts under the home folder of the user, inside the:<pre>
     * ~/{@value com.aemreunal.config.GlobalSettings#ROOT_STORAGE_FOLDER_DIRECTORY_NAME}/{@value
     * com.aemreunal.config.GlobalSettings#TEXT_STORAGE_FOLDER_DIRECTORY_NAME}/</pre>
     * folder. The texts will be put in a sub-folder structure like:<pre>
     * &lt;user name&gt;/&lt;project ID&gt;/&lt;region ID&gt;/&lt;beacon ID&gt;/</pre>
     * under the main storage folder.
     * <p>
     * If any of the folders above do not exist, they will be created.
     *
     * @param username
     *         The username of whoever is saving the text.
     * @param projectId
     *         The ID of the project which the region (the text belongs to) is a part of.
     * @param regionId
     *         The ID of the region (the beacon belongs to) of the text.
     * @param beaconId
     *         The ID of the beacon of the text.
     * @param locationInfoTextFile
     *         The text as a {@link MultipartFile}.
     *
     * @return The name of the saved text file as a {@link String}.
     *
     * @throws TextSaveException
     *         If the text file can't be saved.
     */
    public String saveText(String username, Long projectId, Long regionId, Long beaconId, MultipartFile locationInfoTextFile)
            throws TextSaveException {
        // Get the file path from the username, project ID, and region ID attributes
        String filePath = getFilePath(username, projectId, regionId, beaconId);

        // Ensure unique file name
        File textFile = getUniqueFile(filePath);

        // Check for the existence of the parent folder and create it
        // if it doesn't exist
        createParentFolder(projectId, regionId, beaconId, textFile);

        // Create the new text file
        createFile(projectId, regionId, beaconId, textFile);

        // Write the text bytes to the new file
        writeTextToFile(projectId, regionId, beaconId, locationInfoTextFile, textFile);

        // Read the text properties to get dimensions
        return textFile.getName();
    }

    /**
     * Loads the specified text file and returns the contents of the text file as a {@link
     * String}.
     *
     * @param username
     *         The username of whoever is loading the text.
     * @param projectId
     *         The ID of the project which the region (the text belongs to) is a part of.
     * @param regionId
     *         The ID of the region (the beacon belongs to) of the text.
     * @param beaconId
     *         The ID of the beacon of the text.
     * @param textFileName
     *         The name of the text file to be loaded.
     *
     * @return The contents of the text file as a {@link String} if the file has been
     * successfully loaded and read, an empty {@link String} otherwise.
     *
     * @throws TextLoadException
     *         If the text file can't be loaded or read.
     */
    public String loadText(String username, Long projectId, Long regionId, Long beaconId, String textFileName)
            throws TextLoadException {
        // Get the file path from the username, project ID, region ID, and beacon ID attributes
        String filePath = getFilePath(username, projectId, regionId, beaconId);
        // Get the text file
        File textFile = new File(filePath + textFileName);
        if (!textFile.exists()) {
            System.err.println("Text file does not exist!");
            throw new TextLoadException(projectId, regionId, beaconId);
        }
        return loadTextFromFile(projectId, regionId, beaconId, textFile);
    }


    /**
     * Deletes the specified text file. The method will do nothing if a {@code null} value
     * or empty string is provided for the {@code textFileName} parameter.
     *
     * @param username
     *         The username of whoever is deleting the text.
     * @param projectId
     *         The ID of the project which the region (the beacon belongs to) is a part
     *         of.
     * @param regionId
     *         The ID of the region of the beacon.
     * @param beaconId
     *         The ID of the beacon of the text.
     * @param textFileName
     *         The name of the text file to be deleted. If a {@code null} value or an
     *         empty string is provided, the method will do nothing and return.
     *
     * @throws TextDeleteException
     *         If the text file can't be deleted.
     */
    public void deleteText(String username, Long projectId, Long regionId, Long beaconId, String textFileName)
            throws TextDeleteException {
        if (textFileName == null || textFileName.equals("")) {
            return;
        }
        // Get the file path from the username, project ID, region ID, and beacon ID attributes
        String filePath = getFilePath(username, projectId, regionId, beaconId);
        // Get the text file
        File textFile = new File(filePath + textFileName);
        try {
            Files.delete(textFile.toPath());
        } catch (NoSuchFileException e) {
            GlobalSettings.err("WARNING: Text file for user: " + username + ", project: " + projectId +
                                       ", region: " + regionId + ", beacon: " + beaconId + ", file name: "
                                       + textFileName + " does not exist, nothing to delete!");
        } catch (IOException e) {
            GlobalSettings.err("Unable to delete the text!");
            throw new TextDeleteException(projectId, regionId, beaconId);
        }
    }

    private String getFilePath(String username, Long projectId, Long regionId, Long beaconId) {
        return GlobalSettings.TEXT_STORAGE_FOLDER_PATH + username + "/" + projectId + "/" + regionId + "/" + beaconId + "/";
    }

    private File getUniqueFile(String filePath) {
        File textFile;
        do {
            String fileName = UUID.randomUUID().toString();
            textFile = new File(filePath + fileName);
        } while (textFile.exists());
        return textFile;
    }

    private void createParentFolder(Long projectId, Long regionId, Long beaconId, File textFile)
    throws TextSaveException {
        if (!textFile.getParentFile().exists()) {
            // If it doesn't exist, create it
            if (!textFile.getParentFile().mkdirs()) {
                System.err.println("Unable to create parent folders!");
                throw new TextSaveException(projectId, regionId, beaconId);
            }
        }
    }

    private void createFile(Long projectId, Long regionId, Long beaconId, File textFile)
    throws TextSaveException {
        try {
            if (!textFile.createNewFile()) {
                System.err.println("Unable to createNewFile()!");
                throw new TextSaveException(projectId, regionId, beaconId);
            }
        } catch (IOException e) {
            System.err.println("Unable to create file!");
            throw new TextSaveException(projectId, regionId, beaconId);
        }
    }

    private String loadTextFromFile(Long projectId, Long regionId, Long beaconId, File textFile)
    throws TextLoadException {
        StringBuilder builder = new StringBuilder();
        try {
            Files.readAllLines(textFile.toPath()).forEach(builder::append);
        } catch (FileNotFoundException e) {
            System.err.println("File to read from is not found!");
            throw new TextLoadException(projectId, regionId, beaconId);
        } catch (IOException e) {
            System.err.println("Unable to read from file!");
            throw new TextLoadException(projectId, regionId, beaconId);
        }
        return builder.toString();
    }

    private void writeTextToFile(Long projectId, Long regionId, Long beaconId, MultipartFile locationInfoMultipartFile, File textFile)
    throws TextSaveException {
        try {
            locationInfoMultipartFile.transferTo(textFile);
        } catch (IOException e) {
            System.err.println("Unable to write text to file!");
            throw new TextSaveException(projectId, regionId, beaconId);
        }
    }
}
