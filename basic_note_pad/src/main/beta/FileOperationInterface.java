package main.beta;

import java.io.File;

public interface FileOperationInterface {
    boolean isSave();

    void setSave(boolean saved);

    String getFileName();

    void setFileName(String fileName);

    boolean saveFile(File temp);

    boolean saveThisFile();

    boolean saveAsFile();

    boolean openFile(File temp);

    void openFile();

    void updateStatus(File temp, boolean saved);

    boolean confirmSave();

    void newFile();
}
