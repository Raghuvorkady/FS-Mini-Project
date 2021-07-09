package main.beta;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class Notepad implements ActionListener, MenuConstants {
    JFrame jFrame;
    JTextArea jTextArea;
    JLabel statusBar;

    FileOperation fileHandler;

    Notepad() {
        jFrame = new JFrame();
        jTextArea = new JTextArea(30, 60);
        statusBar = new JLabel("||       Ln 1, Col 1  ", JLabel.RIGHT);
        jFrame.add(new JScrollPane(jTextArea), BorderLayout.CENTER);
        jFrame.add(statusBar, BorderLayout.SOUTH);
        jFrame.add(new JLabel("  "), BorderLayout.EAST);
        jFrame.add(new JLabel("  "), BorderLayout.WEST);
        createMenuBar(jFrame);

        jFrame.pack();
        jFrame.setLocation(100, 50);
        jFrame.setVisible(true);
        jFrame.setLocation(150, 50);
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        fileHandler = new FileOperation(this);

        jTextArea.addCaretListener(
                event -> {
                    int lineNumber = 0, column = 0, pos = 0;

                    try {
                        pos = jTextArea.getCaretPosition();
                        lineNumber = jTextArea.getLineOfOffset(pos);
                        column = pos - jTextArea.getLineStartOffset(lineNumber);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (jTextArea.getText().length() == 0) {
                        lineNumber = 0;
                        column = 0;
                    }
                    statusBar.setText("||       Ln " + (lineNumber + 1) + ", Col " + (column + 1));
                });

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                fileHandler.saved = false;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                fileHandler.saved = false;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                fileHandler.saved = false;
            }
        };
        jTextArea.getDocument().addDocumentListener(documentListener);

        WindowListener windowListener = new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (fileHandler.confirmSave()) System.exit(0);
            }
        };
        jFrame.addWindowListener(windowListener);
    }

    private void createMenuBar(JFrame jFrame) {
        JMenuBar jMenuBar = new JMenuBar();

        // file menu
        createFileMenu(jMenuBar);

        // view menu
        createViewMenu(jMenuBar);

        // help menu
        createHelpMenu(jMenuBar);

        jFrame.setJMenuBar(jMenuBar);
    }

    private void createFileMenu(JMenuBar jMenuBar) {
        JMenuItem jMenuItemTemp;
        JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, jMenuBar);
        createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
        createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
        createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
        createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, this);
        fileMenu.addSeparator();

        createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);
    }

    private void createViewMenu(JMenuBar jMenuBar) {
        JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, jMenuBar);
        createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);
    }

    private void createHelpMenu(JMenuBar jMenuBar) {
        JMenu helpMenu = createMenu(helpText, KeyEvent.VK_H, jMenuBar);
        createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this);
    }

    private JMenu createMenu(String s, int key, JMenuBar toMenuBar) {
        JMenu jMenu = new JMenu(s);
        jMenu.setMnemonic(key);
        toMenuBar.add(jMenu);
        return jMenu;
    }

    private JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener actionListener) {
        JMenuItem jMenuItem = new JMenuItem(s, key);
        jMenuItem.addActionListener(actionListener);
        jMenuItem.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
        toMenu.add(jMenuItem);
        return jMenuItem;
    }

    private JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener actionListener) {
        JMenuItem jMenuItem = new JMenuItem(s, key);
        jMenuItem.addActionListener(actionListener);
        toMenu.add(jMenuItem);
        return jMenuItem;
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
        JCheckBoxMenuItem jCheckBoxMenuItem = new JCheckBoxMenuItem(s);
        jCheckBoxMenuItem.setMnemonic(key);
        jCheckBoxMenuItem.addActionListener(al);
        jCheckBoxMenuItem.setSelected(false);
        toMenu.add(jCheckBoxMenuItem);
        return jCheckBoxMenuItem;
    }

    // a function written in ActionListener interface
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String actionCommand = actionEvent.getActionCommand();
        switch (actionCommand) {
            case fileNew:
                fileHandler.newFile();
                break;
            case fileOpen:
                fileHandler.openFile();
                break;
            case fileSave:
                fileHandler.saveThisFile();
                break;
            case fileSaveAs:
                fileHandler.saveAsFile();
                break;
            case fileExit:
                if (fileHandler.confirmSave()) System.exit(0);
                break;
            case viewStatusBar:
                JCheckBoxMenuItem jCheckBoxMenuItem2 = (JCheckBoxMenuItem) actionEvent.getSource();
                statusBar.setVisible(jCheckBoxMenuItem2.isSelected());
                break;
            case helpAboutNotepad:
                JOptionPane.showMessageDialog(Notepad.this.jFrame, aboutText, "About",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                statusBar.setText("This " + actionCommand + " command is yet to be implemented");
                break;
        }
    }
}