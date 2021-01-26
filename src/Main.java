import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) {
        String dirSaveGame = System.getProperty("user.dir") + "//Games" + "//savegames";

        extractZip(dirSaveGame);
        viewState(dirSaveGame);
    }

    private static void extractZip(String path) {
        String pathToZip = path + "//saves.zip";

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(pathToZip))) {
            ZipEntry zipEntry;
            String name;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                name = zipEntry.getName();
                FileOutputStream fileOutputStream = new FileOutputStream(path + "//" + name);
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    fileOutputStream.write(c);
                }
                fileOutputStream.flush();
                zipInputStream.closeEntry();
                fileOutputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<File> getListSave(String path) {
        File dir = new File(path);
        List<File> listSavedGames = new ArrayList<>();

        if (dir.isDirectory()) {
            List<File> allFiles = Arrays.asList(dir.listFiles());

            //Фильтруем
            listSavedGames = allFiles.stream().filter(file -> file.getName().matches(".*\\.dat")).collect(Collectors.toList());
        }
        return listSavedGames;
    }

    private static void viewState(String path) {
        System.out.println("Список сохранений: ");
        for (File file : getListSave(path)) {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                GameProgress gameProgress = (GameProgress) objectInputStream.readObject();
                System.out.println("Файл " + file.getName() + ": " + gameProgress.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}