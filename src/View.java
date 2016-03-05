import javax.swing.*;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class View extends JFrame implements Observer{
    Model model;
    JFileChooser fc;
    JMenuBar menubar;
    public View(Model m){
        createMenuBar();
        model = m;
    }
    private void createMenuBar() {
        menubar = new JMenuBar();

        ImageIcon iconNew = new ImageIcon(getClass().getResource("icons/new.png"));
        ImageIcon iconOpen = new ImageIcon(getClass().getResource("icons/open.png"));
        ImageIcon iconSave = new ImageIcon(getClass().getResource("icons/save.png"));
        ImageIcon iconExit = new ImageIcon(getClass().getResource("icons/exit.png"));
        fc = new JFileChooser(){
            @Override
            public void approveSelection(){
                File f = getSelectedFile();
                if(f.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }
        };
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        FileNameExtensionFilter filterText = new FileNameExtensionFilter("*.txt", "txt");
        FileNameExtensionFilter filterBinary = new FileNameExtensionFilter("*.binary", "binary");
        fc.setFileFilter(filterText);
        fc.setFileFilter(filterBinary);

        JMenuItem newMi = new JMenuItem("New", iconNew);
        JMenuItem openMi = new JMenuItem("Open", iconOpen);
        JMenuItem saveMi = new JMenuItem("Save", iconSave);
        JMenuItem exitMi = new JMenuItem("Exit", iconExit);
        newMi.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
        openMi.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
        saveMi.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
        exitMi.setToolTipText("Exit application");
        exitMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                if(model.isUnsavedProgress()){
                    int result = JOptionPane.showConfirmDialog(View.this, "Your unsaved changes will be lost. Save first?","Unsaved Changes",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            View.this.saveFile();
                            System.exit(0);
                            return;
                        case JOptionPane.NO_OPTION:
                            System.exit(0);
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            System.exit(0);
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            return;
                    }
                }
                System.exit(0);
            }
        });
        newMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.isUnsavedProgress()){
                    int result = JOptionPane.showConfirmDialog(View.this, "Your unsaved changes will be lost. Save first?","Unsaved Changes",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            View.this.saveFile();
                            return;
                        case JOptionPane.NO_OPTION:
                            model.newFile();
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            return;
                    }
                }
                model.newFile();
            }
        });
        openMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(model.isUnsavedProgress()){
                    int result = JOptionPane.showConfirmDialog(View.this, "Your unsaved changes will be lost. Save first?","Unsaved Changes",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            View.this.saveFile();
                            View.this.openFile();
                            return;
                        case JOptionPane.NO_OPTION:
                            View.this.openFile();
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            return;
                    }
                }
                openFile();
            }
        });
        saveMi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(model.isUnsavedProgress()){
                    int result = JOptionPane.showConfirmDialog(View.this, "Your unsaved changes will be lost. Save first?","Unsaved Changes",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            View.this.saveFile();
                            System.exit(0);
                            return;
                        case JOptionPane.NO_OPTION:
                            System.exit(0);
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            System.exit(0);
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            return;
                    }
                }
                System.exit(0);
            }
        });

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem fit = new JRadioButtonMenuItem("Fit to Screen");
        JRadioButtonMenuItem fixed = new JRadioButtonMenuItem("Fixed size");
        fixed.setSelected(true);
        fit.setSelected(false);
        group.add(fit);
        group.add(fixed);
        fit.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                model.setViewOption("fit");
            }
        });
        fixed.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                model.setViewOption("fixed");
            }
        });

        viewMenu.add(fit);
        viewMenu.add(fixed);

        fileMenu.add(newMi);
        fileMenu.add(openMi);
        fileMenu.add(saveMi);
        fileMenu.addSeparator();
        fileMenu.add(exitMi);

        menubar.add(fileMenu);
        menubar.add(viewMenu);

        setJMenuBar(menubar);
    }

    public void saveFile(){
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if(filePath.endsWith(".txt")){
                model.saveAsText(selectedFile);
            }
            if(filePath.endsWith(".binary")){
                model.saveAsBinary(selectedFile);
            }
        }
    }

    public void openFile(){
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            if(filePath.endsWith(".txt")){
                model.loadTextFile(selectedFile);
            }
            if(filePath.endsWith(".binary")){
                model.loadBinary(selectedFile);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }

    private class MenuItemAction extends AbstractAction {
        public MenuItemAction(String text, ImageIcon icon, Integer mnemonic) {
            super(text);
            putValue(SMALL_ICON, icon);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            System.out.println(e.getActionCommand());
        }
    }
}
