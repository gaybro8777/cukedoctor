package com.github.cukedoctor.extension;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by pestano on 16/08/15.
 */
public class FileUtil {

    public static String readFileContent(File target) {
        StringBuilder content = new StringBuilder();
        try {

            InputStream openStream = new FileInputStream(target);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(openStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            bufferedReader.close();

        } catch (Exception e) {
        }
        return content.toString();
    }

    public static File loadTestFile(String fileName) {
        return new File(Paths.get("").toAbsolutePath().toString() + "/target/test-classes/" + fileName);
    }
}