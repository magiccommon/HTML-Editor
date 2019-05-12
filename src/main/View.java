package main;

import listeners.FrameListener;
import listeners.TabbedPaneChangeListener;
import listeners.UndoListener;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class View extends JFrame implements ActionListener {
    private Controller controller;
    private JTabbedPane tabbedPane = new JTabbedPane();
    private JTextPane htmlTextPane = new JTextPane();
    private JEditorPane plainTextPane = new JEditorPane();
    private UndoManager undoManager = new UndoManager();
    private UndoListener undoListener = new UndoListener(undoManager);

    public View() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    public Controller getController() {
        return controller;
    }

    public UndoListener getUndoListener() {
        return undoListener;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String command = actionEvent.getActionCommand();
        if (command.equals("New")) controller.createNewDocument();
        if (command.equals("Open")) controller.openDocument();
        if (command.equals("Save")) controller.saveDocument();
        if (command.equals("Save As")) controller.saveDocumentAs();
        if (command.equals("Exit")) exit();
        if (command.equals("About")) showAbout();

    }

    public boolean canUndo(){
        return undoManager.canUndo();
    }

    public boolean canRedo(){
        return undoManager.canRedo();
    }

    public void resetUndo(){
        undoManager.discardAllEdits();
    }

    public void init() {
        initGui();
        addWindowListener(new FrameListener(this));
        setVisible(true);
    }

    public void undo() {
        try {
            undoManager.undo();
        } catch (CannotUndoException e) {
            ExceptionHandler.log(e);
        }
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException e) {
            ExceptionHandler.log(e);
        }
    }

    public boolean isHtmlTabSelected() {
        return tabbedPane.getSelectedIndex() == 0;
    }

    public void selectHtmlTab() {
        tabbedPane.setSelectedIndex(0);
        resetUndo();
    }

    public void update() {
        htmlTextPane.setDocument(controller.getDocument());
    }

    public void showAbout() {
        JOptionPane about = new JOptionPane();
        String ms = "Created by Dmitrii Medvedev\nGitHub: github.com/magiccommon";
        JOptionPane.showMessageDialog(about, ms, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public void selectedTabChanged() {
        if (isHtmlTabSelected()) controller.setPlainText(plainTextPane.getText());
        else plainTextPane.setText(controller.getPlainText());
        resetUndo();
    }
    /*
    Alternative code:

    public void selectedTabChanged() {
        if (tabbedPane.getSelectedIndex() == 0) controller.setPlainText(plainTextPane.getText());
        else if (tabbedPane.getSelectedIndex() == 1) plainTextPane.setText(controller.getPlainText());
        resetUndo();
    }
     */

    public void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        MenuHelper.initFileMenu(this, menuBar);
        MenuHelper.initEditMenu(this, menuBar);
        MenuHelper.initStyleMenu(this, menuBar);
        MenuHelper.initAlignMenu(this, menuBar);
        MenuHelper.initColorMenu(this, menuBar);
        MenuHelper.initFontMenu(this, menuBar);
        MenuHelper.initHelpMenu(this, menuBar);
        getContentPane().add(menuBar, BorderLayout.NORTH);
    }

    //Creating two tabs
    public void initEditor() {
        htmlTextPane.setContentType("text/html");

        JScrollPane htmlPane = new JScrollPane(htmlTextPane);
        tabbedPane.addTab("Page", htmlPane);

        JScrollPane textPane = new JScrollPane(plainTextPane);
        tabbedPane.addTab("HTML", textPane);

        tabbedPane.setPreferredSize(new Dimension(800, 400));

        TabbedPaneChangeListener listener = new TabbedPaneChangeListener(this);
        tabbedPane.addChangeListener(listener);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public void initGui() {
        initMenuBar();
        initEditor();
        pack();
    }

    public void exit() {
        controller.exit();
    }
}

