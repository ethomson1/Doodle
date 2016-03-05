import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Doodle {
    public static void main(String[] args){
        // create Model and initialize it
        Model model = new Model();

        // create colour palette, canvas and animation components
        ColourPalette tools = new ColourPalette(model);
        Playback animation = new Playback(model);
        Canvas canvas = new Canvas(model);

        // Create main BorderLayout
        JPanel main = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(canvas);

        // Add subcomponents to appropriate areas of BorderLayout
        main.add(tools, BorderLayout.WEST);
        main.add(animation, BorderLayout.SOUTH);
        main.add(scrollPane, BorderLayout.CENTER);

        // Create window with menus
        JFrame view = new View(model);
        view.getContentPane().add(main);

        // tell Model about Views.
        //model.addObserver(view);
        model.addObserver(tools);
        model.addObserver(animation);
        model.addObserver(canvas);

        // let all the views know that they're connected to the model
        model.notifyObservers();

        view.setPreferredSize(new Dimension(640,480));
        view.setMinimumSize(new Dimension(400,360));
        view.pack();
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setVisible(true);
    }
}
