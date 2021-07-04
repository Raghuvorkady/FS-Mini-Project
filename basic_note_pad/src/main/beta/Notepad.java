package main.beta;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class Notepad implements ActionListener, MenuConstants {
    JFrame jFrame;
    JTextArea jTextArea;
    JLabel statusBar;

    private final boolean saved = true;

    String searchString, replaceString;
    int lastSearchIndex;

    FileOperation fileHandler;

    JDialog findReplaceDialog = null;
    JColorChooser bgColorChooser = null;
    JColorChooser fontColorChooser = null;
    JDialog backgroundDialog = null;
    JDialog foregroundDialog = null;
    JMenuItem cutItem, copyItem, deleteItem, findItem, findNextItem,
            replaceItem, gotoItem, selectAllItem;

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
            case editCut:
                jTextArea.cut();
                break;
            case editCopy:
                jTextArea.copy();
                break;
            case editPaste:
                jTextArea.paste();
                break;
            case editDelete:
                jTextArea.replaceSelection("");
                break;
            case editGoTo:
                if (Notepad.this.jTextArea.getText().length() == 0)
                    return;
                goTo();
                break;
            case editSelectAll:
                jTextArea.selectAll();
                break;
            case editTimeDate:
                jTextArea.insert(new Date().toString(), jTextArea.getSelectionStart());
                break;
            case formatWordWrap: {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) actionEvent.getSource();
                jTextArea.setLineWrap(temp.isSelected());
                break;
            }

            case formatForeground:
                showForegroundColorDialog();
                break;
            case formatBackground:
                showBackgroundColorDialog();
                break;
            case viewStatusBar: {
                JCheckBoxMenuItem temp = (JCheckBoxMenuItem) actionEvent.getSource();
                statusBar.setVisible(temp.isSelected());
                break;
            }
            case helpAboutNotepad:
                JOptionPane.showMessageDialog(Notepad.this.jFrame, aboutText, "About",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                statusBar.setText("This " + actionCommand + " command is yet to be implemented");
                break;
        }
    }

    private void goTo() {
        int lineNumber = 0;
        try {
            lineNumber = jTextArea.getLineOfOffset(jTextArea.getCaretPosition()) + 1;
            String tempStr = JOptionPane.showInputDialog(jFrame, "Enter Line Number:", "" + lineNumber);
            if (tempStr == null) {
                return;
            }
            lineNumber = Integer.parseInt(tempStr);
            jTextArea.setCaretPosition(jTextArea.getLineStartOffset(lineNumber - 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBackgroundColorDialog() {
        if (bgColorChooser == null)
            bgColorChooser = new JColorChooser();
        if (backgroundDialog == null)
            backgroundDialog = JColorChooser.createDialog
                    (Notepad.this.jFrame,
                            formatBackground,
                            false,
                            bgColorChooser,
                            event -> Notepad.this.jTextArea.setBackground(bgColorChooser.getColor()),
                            null);

        backgroundDialog.setVisible(true);
    }

    private void showForegroundColorDialog() {
        if (fontColorChooser == null)
            fontColorChooser = new JColorChooser();
        if (foregroundDialog == null)
            foregroundDialog = JColorChooser.createDialog
                    (Notepad.this.jFrame,
                            formatForeground,
                            false,
                            fontColorChooser,
                            event -> Notepad.this.jTextArea.setForeground(fontColorChooser.getColor()),
                            null);

        foregroundDialog.setVisible(true);
    }

    private JMenuItem createMenuItem(String s, int key, JMenu toMenu, ActionListener al) {
        JMenuItem temp = new JMenuItem(s, key);
        temp.addActionListener(al);
        toMenu.add(temp);

        return temp;
    }

    private JMenuItem createMenuItem(String s, int key, JMenu toMenu, int aclKey, ActionListener al) {
        JMenuItem temp = new JMenuItem(s, key);
        temp.addActionListener(al);
        temp.setAccelerator(KeyStroke.getKeyStroke(aclKey, ActionEvent.CTRL_MASK));
        toMenu.add(temp);
        return temp;
    }

    private JCheckBoxMenuItem createCheckBoxMenuItem(String s,
                                                     int key, JMenu toMenu, ActionListener al) {
        JCheckBoxMenuItem temp = new JCheckBoxMenuItem(s);
        temp.setMnemonic(key);
        temp.addActionListener(al);
        temp.setSelected(false);
        toMenu.add(temp);

        return temp;
    }

    private JMenu createMenu(String s, int key, JMenuBar toMenuBar) {
        JMenu temp = new JMenu(s);
        temp.setMnemonic(key);
        toMenuBar.add(temp);
        return temp;
    }

    private void createMenuBar(JFrame f) {
        JMenuBar mb = new JMenuBar();
        JMenuItem temp;

        // file menu
        JMenu fileMenu = createMenu(fileText, KeyEvent.VK_F, mb);
        createMenuItem(fileNew, KeyEvent.VK_N, fileMenu, KeyEvent.VK_N, this);
        createMenuItem(fileOpen, KeyEvent.VK_O, fileMenu, KeyEvent.VK_O, this);
        createMenuItem(fileSave, KeyEvent.VK_S, fileMenu, KeyEvent.VK_S, this);
        createMenuItem(fileSaveAs, KeyEvent.VK_A, fileMenu, this);
        fileMenu.addSeparator();

        temp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
        temp.setEnabled(false);

        fileMenu.addSeparator();
        createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);

        // edit menu
        JMenu editMenu = createMenu(editText, KeyEvent.VK_E, mb);
        cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
        copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
        createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
        deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editMenu.addSeparator();

        temp = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, this);
        temp.setEnabled(false);
        editMenu.addSeparator();

        gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
        editMenu.addSeparator();
        selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
        createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this)
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));


        // format menu
        JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, mb);
        createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);

        formatMenu.addSeparator();
        createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
        createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);

        // view menu
        JMenu viewMenu = createMenu(viewText, KeyEvent.VK_V, mb);
        createCheckBoxMenuItem(viewStatusBar, KeyEvent.VK_S, viewMenu, this).setSelected(true);

        // help menu
        JMenu helpMenu = createMenu(helpText, KeyEvent.VK_H, mb);
        createMenuItem(helpAboutNotepad, KeyEvent.VK_A, helpMenu, this);

        MenuListener editMenuListener = new MenuListener() {
            public void menuSelected(MenuEvent menuEvent) {
                if (Notepad.this.jTextArea.getText().length() == 0) {
                    selectAllItem.setEnabled(false);
                    gotoItem.setEnabled(false);
                } else {
                    selectAllItem.setEnabled(true);
                    gotoItem.setEnabled(true);
                }
                if (Notepad.this.jTextArea.getSelectionStart() == jTextArea.getSelectionEnd()) {
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                } else {
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                }
            }

            public void menuDeselected(MenuEvent menuEvent) {
            }

            public void menuCanceled(MenuEvent menuEvent) {
            }
        };
        editMenu.addMenuListener(editMenuListener);
        f.setJMenuBar(mb);
    }
}