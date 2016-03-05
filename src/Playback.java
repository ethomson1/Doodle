import javax.swing.*;
import javax.swing.event.*;
import java.util.Observer;
import java.util.Observable;
import java.awt.*;
import java.awt.event.*;

public class Playback extends JPanel implements Observer{
    private Model model;
    private JButton play;
    private JButton start;
    private JButton end;
    private JSlider slider;
    private boolean sliderValueChanged = false;

    public Playback(Model model_){
        model = model_;
        play = new JButton("Play");
        play.setEnabled(false);
        start = new JButton("Start");
        end = new JButton("End");
        slider = new JSlider(0,0);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(play);
        add(slider);
        add(start);
        add(end);
        play.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                model.replay(slider.getValue());
            }
        });
        start.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                model.rewind(-1);
            }
        });
        end.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                model.rewind(slider.getMaximum());
                slider.setValue(slider.getMaximum());
            }
        });
        slider.addChangeListener(new SliderListener());
    }
    @Override
    public void update(Observable arg0, Object arg1) {
        if(model.numLines() > 0){
            play.setEnabled(true);
        }
        slider.setMaximum(model.numLines());
        slider.setValue(model.getPlaybackStart());
        if(model.isDone_replay()){
            slider.setValue(slider.getMaximum());
        }
    }
    class SliderListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            if(source.getValueIsAdjusting()) sliderValueChanged = true;
            if(!source.getValueIsAdjusting() && sliderValueChanged) {
                int value = (int) source.getValue();
                model.setUndo(true);
                model.rewind(value);
                sliderValueChanged = false;
            }
        }
    }
}
