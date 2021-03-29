package budge.service;

import budge.Main;
import budge.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class BackupService {

    // global ivar
    private final String appSupportPath = System.getProperty("user.home") + "/Library/Application Support/Budge/";
    private final String backupDirPath = "backup/";

    /**
     * Method that copies the contents of the repository directory to Application Support
     * @throws IOException
     */
    public void backup() throws IOException {
        // create the backup directory if it doesn't already exist
        File backupFolder = new File(appSupportPath + backupDirPath);
        if (!backupFolder.exists()) {
            if (backupFolder.mkdirs()) {
                Main.getFrame().updateConsole("Support folder successfully created!");
            }
        }

        // do the backup
        File repo = new File("src/resources/repository/");
        if (repo.exists()) {
            // check to see if backup folder is empty
            // if it's not empty, delete all contents in it
            if (!isEmpty(backupFolder.toPath())) {
                deleteFilesInFolder(backupFolder);
            }

            // do the copy
            copyDirectory(repo.getPath(),
                    backupFolder.getPath().concat("/repo_backup_").concat(Utils.getCurrentTimestampForFileName()));
        } else {
            // repository directory wasn't found
            Main.getFrame().updateConsole("Repository directory not found!");
        }
    }

    /**
     * Copies a directory and all of its contents
     * @param sourceDirectoryLocation, the source folder
     * @param destinationDirectoryLocation, the destination path
     * @throws IOException
     */
    private void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation)).forEach(source -> {
            Path destination = Paths.get(destinationDirectoryLocation,
                    source.toString().substring(sourceDirectoryLocation.length()));
            try {
                Files.copy(source, destination);
            } catch (IOException e) {
                Main.getFrame().updateConsole("IOException while copying files! " + e.getMessage());
            }
        });
    }

    /**
     * Checks to see if the folder is empty
     * @param path, the path of the folder to check
     * @return the result of the check
     * @throws IOException
     */
    private boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }

    /**
     * Deletes all files within the folder
     * This assumes that there are only files within that folder, no other internal folders
     * @param folder, the directory to delete
     */
    private void deleteFilesInFolder(File folder) {
        for(File file: Objects.requireNonNull(folder.listFiles())) {
            if (!file.delete()) {
                // backup folder is not empty, let's delete all files in it
                File[] allContents = file.listFiles();
                if (allContents != null) {
                    // for each file (which should only be csv files), delete
                    for (File csvFile : allContents) {
                        if (!csvFile.delete()) {
                            Main.getFrame().updateConsole("Error while deleting old backups!");
                        }
                    }
                }
                // folder should not be empty, if there's an issue deleting now, there's another issue
                if (!file.delete()) {
                    Main.getFrame().updateConsole("Error while deleting old backup directory!");
                }
            }
        }
    }
}
