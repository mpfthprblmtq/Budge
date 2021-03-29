package budge.utils;

import budge.Main;
import budge.service.DialogService;

import java.io.File;
import java.io.IOException;

public class FIleUtils {

    // globals
    static DialogService dialogService = Main.getDialogService();

    public static boolean initializeFile(String filePath) {
        File file = new File(filePath);
        String name = file.getName();
        String path = file.getPath().replace(name, StringUtils.EMPTY);

        try {

            // check to see if the repository folder exists
            if (!new File(path).exists()) {
                if (!new File(path).mkdirs()) {
                    throw new IOException("Couldn't create support directory!\nPath: " + path);
                }
            }

            // check to see if the file exists
            if (!new File(filePath).exists()) {
                if (file.createNewFile()) {
                    Main.addToInitialMessage(Constants.NEWLINE + "Created new " + name + " file!");
                } else {
                    throw new IOException("Couldn't create " + name);
                }
            }
            return true;
        } catch (IOException e) {
            dialogService.showErrorDialog("Error", e.getMessage(), Main.getFrame());
            return false;
        }
    }
}
