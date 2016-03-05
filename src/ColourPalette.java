import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;
import java.util.Observable;
import java.awt.*;
import java.awt.event.*;

public class ColourPalette extends JPanel implements Observer {
    private Model model;
    private JLabel cur_colour;
    private JToolBar colour_picker;
    private JToolBar stroke_thickness;
    private ArrayList<Color> colours = new ArrayList<>(Arrays.asList(Color.BLACK, Color.WHITE, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.ORANGE));
    private ArrayList<JButton> colour_buttons = new ArrayList<>();
    private ArrayList<JButton> thickness_buttons = new ArrayList<>();
    private ArrayList<Integer> thicknesses = new ArrayList<>(Arrays.asList(1, 3, 5, 8));

    public ColourPalette(Model model_){
        model = model_;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        colour_picker = new JToolBar();
        stroke_thickness = new JToolBar();
        cur_colour = new JLabel("Colour");
        cur_colour.setBackground(model.getColour());
        cur_colour.setForeground(model.getColour());
        cur_colour.setVisible(true);
        cur_colour.setOpaque(true);
        createColourToolbar();
        createStrokeToolbar();
        int index = thicknesses.indexOf(model.getThickness());
        thickness_buttons.get(index).setBackground(Color.LIGHT_GRAY);
        add(Box.createVerticalGlue());
        for (int i = 0; i < colours.size(); i++){
            JButton colour = colour_buttons.get(i);
            final int finalI = i;
            colour.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    model.changeColour(colours.get(finalI));
                }
            });
        }
        for (int i = 0; i < thickness_buttons.size(); i++){
            JButton thickness = thickness_buttons.get(i);
            final int finalI = i;
            thickness.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    model.changeThickness(thicknesses.get(finalI));
                }
            });
        }
        JButton custom_chooser = colour_buttons.get(colour_buttons.size()-1); // last colour should be the custom chooser
        custom_chooser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                Color c = JColorChooser.showDialog(null, "Choose a Color", Color.white);
                if (c != null){
                    model.changeColour(c);
                }
            }
        });
        this.setVisible(true);
    }

    private void createColourToolbar(){
        colour_picker.setLayout(new GridLayout(6,2));

        ImageIcon colourSwatch = new ImageIcon(getClass().getResource("icons/black.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/white.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/red.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/green.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/blue.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/yellow.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/pink.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/aqua.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/orange.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        colourSwatch = new ImageIcon(getClass().getResource("icons/eyedropper.png"));
        colour_buttons.add(new JButton("", colourSwatch));

        for(int i = 0; i < colour_buttons.size(); i++) {
            colour_picker.add(colour_buttons.get(i));
            colour_buttons.get(i).setMargin(new Insets(0, 0, 0, 0));
            colour_buttons.get(i).setContentAreaFilled(false);
            colour_buttons.get(i).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        colour_picker.add(cur_colour);
        add(colour_picker);
    }

    private void createStrokeToolbar(){
        stroke_thickness.setLayout(new GridLayout(4,1));

        ImageIcon lineSwatch = new ImageIcon(getClass().getResource("icons/thin.png"));
        thickness_buttons.add(new JButton("", lineSwatch));

        lineSwatch = new ImageIcon(getClass().getResource("icons/medium.png"));
        thickness_buttons.add(new JButton("", lineSwatch));

        lineSwatch = new ImageIcon(getClass().getResource("icons/thick.png"));
        thickness_buttons.add(new JButton("", lineSwatch));

        lineSwatch = new ImageIcon(getClass().getResource("icons/extra_thick.png"));
        thickness_buttons.add(new JButton("", lineSwatch));

        for(int i = 0; i < thickness_buttons.size(); i++) {
            stroke_thickness.add(thickness_buttons.get(i));
            thickness_buttons.get(i).setMargin(new Insets(0,2,0,2));
            thickness_buttons.get(i).setFocusPainted(false);
//            thickness_buttons.get(i).setContentAreaFilled(false);
            thickness_buttons.get(i).setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        add(stroke_thickness);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        // Set colour
        cur_colour.setBackground(model.getColour());
        cur_colour.setForeground(model.getColour());
        // Clear current selection for thickness
        for (int i = 0; i < thickness_buttons.size(); i++){
            thickness_buttons.get(i).setBackground(null);
        }
        // Set thickness
        int index = thicknesses.indexOf(model.getThickness());
        thickness_buttons.get(index).setBackground(Color.LIGHT_GRAY);
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        repaint();
    }
}
