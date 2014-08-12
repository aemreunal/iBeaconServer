package com.aemreunal.helper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import static com.aemreunal.helper.Connection.getConnection;

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

/*
 * Via: http://www.dbunit.org/faq.html#extract
 */

public class DatabaseHelper {
    public static final String OUTPUT_FILE_PATH = "src/test/resources/datasets/test.xml";

    public boolean extractFromDB() {
        return dumpDatabase(getConnection());
    }

    private boolean dumpDatabase(IDatabaseConnection connection) {
        try {
            IDataSet fullDataSet = connection.createDataSet();
            FlatXmlDataSet.write(fullDataSet, new FileOutputStream(OUTPUT_FILE_PATH));
            return true;
        } catch (SQLException e) {
            System.err.println("Unable to create dataset!");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.err.println("Unable to create output file!");
            e.printStackTrace();
        } catch (DataSetException e) {
            System.err.println("Error when writing dataset!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Unable to write to output file!");
            e.printStackTrace();
        }
        return false;
    }
}
