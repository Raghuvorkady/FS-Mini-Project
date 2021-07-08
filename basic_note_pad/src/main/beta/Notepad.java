package main.beta;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public class Notepad implements ActionListener, MenuConstants {
    JFrame jFrame;
    JTextArea jTextArea;
    JLabel statusBar;

    FileOperation fileHandler;

    JColorChooser bgColorChooser = null;
    JColorChooser fontColorChooser = null;
    JDialog backgroundDialog = null;
    JDialog foregroundDialog = null;
    JMenuItem cutItem;
    JMenuItem copyItem;
    JMenuItem deleteItem;
    JMenuItem gotoItem;
    JMenuItem selectAllItem;

    Notepad() {
        jFrame = new JFrame();
        jTextArea = new JTextArea(30, 60);
        statusBar = new JLabel("||       Ln 1, Col 1  ", JLabel.RIGHT);
        jFrame.add(new JScrollPane(jTextArea), BorderLayout.CENTER);
        jFrame.add(statusBar, BorderLayout.SOUTH);
        jFrame.add(new JLabel("  "), BorderLayout.EAST); // side padding/margin
        jFrame.add(new JLabel("  "), BorderLayout.WEST);
        createMenuBar(jFrame);

        jFrame.pack();
        jFrame.setLocation(100, 50);
        jFrame.setVisible(true);
        jFrame.setLocation(150, 50);
        jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        fileHandler = new FileOperation(this);

        jTextArea.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent event) {
                int lineNumber = 0, column = 0, pos = 0;

                try {
                    pos = jTextArea.getCaretPosition();
                    lineNumber = jTextArea.getLineOfOffset(pos);
                    column = pos - jTextArea.getLineStartOffset(lineNumber);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                statusBar.setText("||       Ln " + (lineNumber + 1) + ", Col " + (column + 1));
            }
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

        // edit menu
        JMenu editMenu = createEditMenu(jMenuBar);

        // format menu
        createFormatMenu(jMenuBar);

        // view menu
        createViewMenu(jMenuBar);

        // help menu
        createHelpMenu(jMenuBar);

        MenuListener editMenuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                if (jTextArea.getText().length() == 0) {
                    selectAllItem.setEnabled(false);
                    gotoItem.setEnabled(false);
                } else {
                    selectAllItem.setEnabled(true);
                    gotoItem.setEnabled(true);
                }
                if (jTextArea.getSelectionStart() != jTextArea.getSelectionEnd()) {
                    cutItem.setEnabled(true);
                    copyItem.setEnabled(true);
                    deleteItem.setEnabled(true);
                } else {
                    cutItem.setEnabled(false);
                    copyItem.setEnabled(false);
                    deleteItem.setEnabled(false);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        };

        editMenu.addMenuListener(editMenuListener);
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

        /*jMenuItemTemp = createMenuItem(filePageSetup, KeyEvent.VK_U, fileMenu, this);
        jMenuItemTemp.setEnabled(false);*/

        // fileMenu.addSeparator();
        createMenuItem(fileExit, KeyEvent.VK_X, fileMenu, this);
    }

    private JMenu createEditMenu(JMenuBar jMenuBar) {
        JMenuItem jMenuItemTemp;
        JMenu editMenu = createMenu(editText, KeyEvent.VK_E, jMenuBar);
        cutItem = createMenuItem(editCut, KeyEvent.VK_T, editMenu, KeyEvent.VK_X, this);
        copyItem = createMenuItem(editCopy, KeyEvent.VK_C, editMenu, KeyEvent.VK_C, this);
        createMenuItem(editPaste, KeyEvent.VK_P, editMenu, KeyEvent.VK_V, this);
        deleteItem = createMenuItem(editDelete, KeyEvent.VK_L, editMenu, this);
        deleteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        editMenu.addSeparator();

        jMenuItemTemp = createMenuItem(editUndo, KeyEvent.VK_U, editMenu, KeyEvent.VK_Z, this);
        jMenuItemTemp.setEnabled(false);
        editMenu.addSeparator();

        gotoItem = createMenuItem(editGoTo, KeyEvent.VK_G, editMenu, KeyEvent.VK_G, this);
        editMenu.addSeparator();
        selectAllItem = createMenuItem(editSelectAll, KeyEvent.VK_A, editMenu, KeyEvent.VK_A, this);
        createMenuItem(editTimeDate, KeyEvent.VK_D, editMenu, this)
                .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        return editMenu;
    }

    private void createFormatMenu(JMenuBar jMenuBar) {
        JMenu formatMenu = createMenu(formatText, KeyEvent.VK_O, jMenuBar);
        createCheckBoxMenuItem(formatWordWrap, KeyEvent.VK_W, formatMenu, this);

        formatMenu.addSeparator();
        createMenuItem(formatForeground, KeyEvent.VK_T, formatMenu, this);
        createMenuItem(formatBackground, KeyEvent.VK_P, formatMenu, this);
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
            case formatWordWrap:
                JCheckBoxMenuItem jCheckBoxMenuItem1 = (JCheckBoxMenuItem) actionEvent.getSource();
                jTextArea.setLineWrap(jCheckBoxMenuItem1.isSelected());
                break;
            case formatForeground:
                showForegroundColorDialog();
                break;
            case formatBackground:
                showBackgroundColorDialog();
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

    private void goTo() {
        try {
            int lineNumber = jTextArea.getLineOfOffset(jTextArea.getCaretPosition()) + 1;
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

    private void showForegroundColorDialog() {
        if (fontColorChooser == null) {
            fontColorChooser = new JColorChooser();
        }
        if (foregroundDialog == null) {
            foregroundDialog = JColorChooser.createDialog(Notepad.this.jFrame, formatForeground, false,
                    fontColorChooser, event -> Notepad.this.jTextArea.setForeground(fontColorChooser.getColor()), null);
        }

        foregroundDialog.setVisible(true);
    }

    private void showBackgroundColorDialog() {
        if (bgColorChooser == null) {
            bgColorChooser = new JColorChooser();
        }
        if (backgroundDialog == null) {
            backgroundDialog = JColorChooser.createDialog(Notepad.this.jFrame, formatBackground, false,
                    bgColorChooser, event -> Notepad.this.jTextArea.setBackground(bgColorChooser.getColor()), null);
        }

        backgroundDialog.setVisible(true);
    }
}