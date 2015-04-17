/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aosproject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author GbadeboAyoade
 */
public class LogPrinter{
        String fileName;
        String message;

        public LogPrinter(String theFilename,String theMessage) {

            message = theMessage;
            fileName = theFilename;
        }

        public synchronized void run() {

            File log = new File(fileName);

            try {
                if (!log.exists()) {

                    log.createNewFile();
                }

                FileWriter fileWriter = new FileWriter(log, true);

                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(message + "\n");
                bufferedWriter.close();

            } catch (IOException e) {
                System.out.println("COULD NOT LOG!!");
            }

        }
    }
