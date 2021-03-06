package budge.service;

import budge.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

public class BackupService {

    // global ivar
    private final String appSupportPath = System.getProperty("user.home") + "/Library/Application Support/Budge/";
    private final String backupDirPath = "Backup/";

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
            if (!isEmpty(backupFolder.toPath())) {
                deleteFilesInFolder(backupFolder);
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);
            copyDirectory(repo.getPath(), backupFolder.getPath().concat("/repo_backup_").concat(date));
        } else {
            Main.getFrame().updateConsole("Repository directy not found!");
        }
    }

    private void copyDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation)
            throws IOException {
        Files.walk(Paths.get(sourceDirectoryLocation))
                .forEach(source -> {
                    Path destination = Paths.get(destinationDirectoryLocation, source.toString()
                            .substring(sourceDirectoryLocation.length()));
                    try {
                        Files.copy(source, destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private boolean isEmpty(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }

    private void deleteFilesInFolder(File folder) {
        for(File file: Objects.requireNonNull(folder.listFiles())) {
            if (!file.delete()) {
                File[] allContents = file.listFiles();
                if (allContents != null) {
                    for (File csvFile : allContents) {
                        if (!csvFile.delete()) {
                            Main.getFrame().updateConsole("Error while deleting old backups!");
                        }
                    }
                }
                if (!file.delete()) {
                    Main.getFrame().updateConsole("Error while deleting old backup directory!");
                }
            }
        }

    }
}
