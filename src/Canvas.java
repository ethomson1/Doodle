import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Observer;
import java.util.Observable;
import java.awt.*;
import java.awt.event.*;

public class Canvas extends JPanel implements Observer {
    private Model model;
    private int xPressed, yPressed;
    private int xReleased, yReleased;
    private boolean new_line;
    private AffineTransform scale;
    private AffineTransform inverseScale;
    public Canvas(Model model_){
        model = model_;
        setBackground(Color.white);
        scale = new AffineTransform();
        this.addMouseListener(new MouseEventHandler());
        this.addMouseMotionListener(new MouseEventHandler());
    }

    class MouseEventHandler extends MouseAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            try {
                inverseScale = scale.createInverse();
                inverseScale.transform(e.getPoint(), e.getPoint());
            } catch (NoninvertibleTransformException ex){
                System.out.println("Could not scale");
            }
            if(e.getX() < model.defaultWidth() && e.getY() < model.defaultHeight() && e.getX() > 0 && e.getY() > 0) {
                model.addPoint(e, new_line);
                new_line = false;
            } else {
                new_line = true;
            }
        }
        @Override
        public void mousePressed(MouseEvent e) {
            xPressed = e.getX();
            yPressed = e.getY();
            try {
                inverseScale = scale.createInverse();
                inverseScale.transform(e.getPoint(), e.getPoint());
            } catch (NoninvertibleTransformException ex){
                System.out.println("Could not scale");
            }
            new_line = true;
            model.undoStrokes();
            model.addPoint(e,true);
            repaint(); //request Swing to refresh display as soon as it can
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            xReleased = e.getX();
            yReleased = e.getY();
            repaint(); //request Swing to refresh display as soon as it can
        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        if(model.getViewOption() == "fixed"){
            int default_width = model.defaultWidth();
            int default_height = model.defaultHeight();
            setPreferredSize(new Dimension(default_width, default_height));
            // grey out non-drawable area if screen is bigger than size of panel
            if(getWidth() > default_width) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(model.defaultWidth()+1, 0, getWidth() - default_width, getHeight());
            }
            if(getHeight() > default_height) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(0, model.defaultHeight()+1, getWidth(), getHeight() - default_height);
            }
        }
        else if(model.getViewOption() == "fit"){
            setPreferredSize(null);
            double scaleX = getWidth() / (double)model.defaultWidth();
            double scaleY = getHeight() / (double)model.defaultHeight();
            model.setScaleX(scaleX);
            model.setScaleY(scaleY);
            g2.scale(model.getScaleX(), model.getScaleY());
            scale = g2.getTransform();
        }
        ArrayList<Line> lines = model.getLines();
        int playback_start = model.getPlaybackStart();
        for (int i = 0; i <= playback_start; i++) {
            if(i < lines.size()){
                paintLine(g, i);
            }
        }
    }

    public void paintLine(Graphics g, int index){
        Graphics2D g2 = (Graphics2D) g; // cast to get 2D drawing methods
        ArrayList<Line> lines = model.getLines();
        Line line = lines.get(index);
        int[] start = line.getStart();
        int[] end = line.getEnd();
        g2.setStroke(new BasicStroke(line.getThickness()));
        g2.setColor(line.getColour());
        g2.drawLine(start[0], start[1], end[0], end[1]);
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        repaint();
    }
}
