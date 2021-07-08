package main.beta;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;

class FileOperation  {
    Notepad notepad;

    boolean saved;
    boolean newFileFlag;
    String fileName;
    String applicationTitle = "NotepadX";

    File fileRef;
    JFileChooser chooser;

    FileOperation(Notepad notepad) {
        this.notepad = notepad;

        saved = true;
        newFileFlag = true;
        fileName = "Untitled";
        fileRef = new File(fileName);
        this.notepad.jFrame.setTitle(fileName + " - " + applicationTitle);

        chooser = new JFileChooser();

        chooser.setCurrentDirectory(new File("."));
    }

    public boolean isSave() {
        return saved;
    }

    public void setSave(boolean saved) {
        this.saved = saved;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean saveFile(File temp) {
        FileWriter fout = null;
        try {
            fout = new FileWriter(temp);
            fout.write(notepad.jTextArea.getText());
        } catch (IOException ioe) {
            updateStatus(temp, false);
            return false;
        } finally {
            try {
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateStatus(temp, true);
        return true;
    }

    public boolean saveThisFile() {
        if (!newFileFlag)
            return saveFile(fileRef);
        return saveAsFile();
    }

    public boolean saveAsFile() {
        File temp = null;
        chooser.setDialogTitle("Save As...");
        chooser.setApproveButtonText("Save Now");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_S);
        chooser.setApproveButtonToolTipText("Click me to save!");

        do {
            if (chooser.showSaveDialog(this.notepad.jFrame) != JFileChooser.APPROVE_OPTION)
                return false;
            temp = chooser.getSelectedFile();
            if (!temp.exists()) break;
            if (JOptionPane.showConfirmDialog(
                    this.notepad.jFrame, "<html>" + temp.getPath() + " already exists.<br>Do you want to replace it?<html>",
                    "Save As", JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION)
                break;
        } while (true);

        return saveFile(temp);
    }

    public boolean openFile(File temp) {
        FileInputStream fin = null;
        BufferedReader din = null;

        try {
            fin = new FileInputStream(temp);
            din = new BufferedReader(new InputStreamReader(fin));
            String str;
            while ((str = din.readLine()) != null) {
                this.notepad.jTextArea.append(str + "\n");
            }

        } catch (IOException ioe) {
            updateStatus(temp, false);
            return false;
        } finally {
            try {
                din.close();
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateStatus(temp, true);
        this.notepad.jTextArea.setCaretPosition(0);
        return true;
    }

    public void openFile() {
        if (!confirmSave()) return;
        chooser.setDialogTitle("Open File...");
        chooser.setApproveButtonText("Open this");
        chooser.setApproveButtonMnemonic(KeyEvent.VK_O);
        chooser.setApproveButtonToolTipText("Click me to open the selected file.!");

        File temp;
        do {
            if (chooser.showOpenDialog(this.notepad.jFrame) != JFileChooser.APPROVE_OPTION)
                return;
            temp = chooser.getSelectedFile();

            if (temp.exists()) break;

            JOptionPane.showMessageDialog(this.notepad.jFrame,
                    "<html>" + temp.getName() + "<br>file not found.<br>" +
                            "Please verify the correct file name was given.<html>",
                    "Open", JOptionPane.INFORMATION_MESSAGE);

        } while (true);

        this.notepad.jTextArea.setText("");

        if (!openFile(temp)) {
            fileName = "Untitled";
            saved = true;
            this.notepad.jFrame.setTitle(fileName + " - " + applicationTitle);
        }
        if (!temp.canWrite())
            newFileFlag = true;
    }

    public void updateStatus(File temp, boolean saved) {
        if (saved) {
            this.saved = true;
            fileName = temp.getName();
            if (!temp.canWrite()) {
                fileName += "(Read only)";
                newFileFlag = true;
            }
            fileRef = temp;
            notepad.jFrame.setTitle(fileName + " - " + applicationTitle);
            notepad.statusBar.setText("File : " + temp.getPath() + " saved/opened successfully.");
            newFileFlag = false;
        } else {
            notepad.statusBar.setText("Failed to save/open : " + temp.getPath());
        }
    }

    public boolean confirmSave() {
        String strMsg = "<html>The text in the " + fileName + " file has been changed.<br>" +
                "Do you want to save the changes?<html>";
        if (!saved) {
            int x = JOptionPane.showConfirmDialog(this.notepad.jFrame, strMsg, applicationTitle,
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (x == JOptionPane.CANCEL_OPTION) return false;
            if (x == JOptionPane.YES_OPTION && !saveAsFile()) return false;
        }
        return true;
    }

    public void newFile() {
        if (!confirmSave()) {
            return;
        }
        this.notepad.jTextArea.setText("");
        fileName = "Untitled";
        fileRef = new File(fileName);
        saved = true;
        newFileFlag = true;
        this.notepad.jFrame.setTitle(fileName + " - " + applicationTitle);
    }
}