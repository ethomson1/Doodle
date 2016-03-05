import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class Model extends Observable {
    private Color cur_colour;
    private int thickness;
    private int playback_start;
    private double scaleX;
    private double scaleY;
    private int default_width;
    private int default_height;
    private ArrayList<Point> points;
    private ArrayList<Line> lines;
    private boolean playback;
    private boolean done_replay;
    private boolean undo;
    private boolean unsavedProgress;
    private String viewOption;
    private Timer timer;
    Model() {
        points = new ArrayList<>();
        lines = new ArrayList<>();
        thickness = 1;
        viewOption = "fixed";
        default_width = 529;
        default_height = 388;
        undo = false;
        cur_colour = Color.black;
        playback_start = -1;
        playback = false;
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(playback_start < lines.size()) {
                    setChanged();
                    notifyObservers();
                    playback_start++;
                } else {
                    timer.stop();
                    doneReplay();
                }
            }
        };
        timer = new Timer(30, actionListener);
        setChanged();
    }
    // Line drawing options
    public Color getColour(){
        return cur_colour;
    }

    public void changeColour(Color colour){
        cur_colour = colour;
        setChanged();
        notifyObservers();
    }

    public int getThickness(){
        return thickness;
    }

    public void changeThickness(int thickness){
        this.thickness = thickness;
        setChanged();
        notifyObservers();
    }
    // Canvas view options
    public void setViewOption(String view){
        viewOption = view;
        if(view == "fixed"){
            scaleY = 1.0;
            scaleX = 1.0;
        }
        setChanged();
        notifyObservers();
    }

    public String getViewOption(){
        return viewOption;
    }

    public int defaultWidth(){
        return default_width;
    }

    public int defaultHeight(){
        return default_height;
    }

    public void setScaleX(double scale){
        scaleX = scale;
    }

    public void setScaleY(double scale){
        scaleY = scale;
    }

    public double getScaleX(){
        return scaleX;
    }

    public double getScaleY(){
        return scaleY;
    }

    // Lines drawn so far
    public ArrayList<Line> getLines(){
        return lines;
    }

    public int numLines(){
        return lines.size();
    }

    public void undoStrokes(){
        if(undo){
            Line start = lines.get(playback_start);
            Line end = lines.get(lines.size()-1);
            int firstPoint = points.lastIndexOf(new Point(start.getStart()[0], start.getStart()[1]));
            int lastPoint = points.lastIndexOf(new Point(end.getEnd()[0], end.getEnd()[1]));
            points.subList(firstPoint, lastPoint).clear();
            lines.subList(playback_start, lines.size()-1).clear();
            undo = false;
            playback = false;
            setChanged();
            notifyObservers();
        }
    }

    public void addPoint(MouseEvent e, boolean new_line){
        unsavedProgress = true;
        points.add(new Point(e.getX(), e.getY()));
        playback_start += 1;
        drawLine(new_line);
    }

    public void drawLine(boolean new_line){
        if(points.size() >=2 ){
            int last_index = points.size()-1;
            Point last = points.get(last_index);
            Point second_last = points.get(last_index-1);
            if(!new_line) {
                lines.add(new Line(cur_colour, thickness, second_last.x, second_last.y, last.x, last.y));
            }
        }
        setChanged();
        notifyObservers();
    }

    public void setUndo(boolean val){
        undo = val;
    }
    // Playback stuff
    public void replay(int value){
        playback = true;
        playback_start = value;
        timer.start();
    }

    public void doneReplay(){
        playback = false;
        done_replay = true;
        setChanged();
        notifyObservers();
        done_replay = false;
        setChanged();
        notifyObservers();
    }

    public boolean isPlayback(){
        return playback;
    }

    public boolean isDone_replay(){
        return done_replay;
    }

    public void rewind(int value){
        playback_start = value;
        playback = true;
        setChanged();
        notifyObservers();
    }

    public int getPlaybackStart(){
        return playback_start;
    }

    public void saveAsText(File file){
        try(PrintWriter output = new PrintWriter(file)){
            for(int i = 0; i < lines.size(); i++){
                Line line = lines.get(i);
                int rgb = line.getColour().getRGB();
                int thickness = line.getThickness();
                int[] start = line.getStart();
                int[] end = line.getEnd();
                output.println(rgb + " " + thickness + " " + start[0] + " " + start[1] + " " + end[0] + " " + end[1]);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        unsavedProgress = false;
    }

    public void loadTextFile(File file){
        lines.clear();
        points.clear();
        try(FileInputStream fis = new FileInputStream(file)){
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while( (line = br.readLine())!= null ){
                String [] tokens = line.split(" ");
                String colour = tokens[0];
                String thickness = tokens[1];
                String x1 = tokens[2];
                String y1 = tokens[3];
                String x2 = tokens[4];
                String y2 = tokens[5];
                int colourInt = Integer.parseInt(colour);
                int thicknessInt = Integer.parseInt(thickness);
                int x1Int = Integer.parseInt(x1);
                int y1Int = Integer.parseInt(y1);
                int x2Int = Integer.parseInt(x2);
                int y2Int = Integer.parseInt(y2);
                Color c = new Color(colourInt);
                points.add(new Point(x1Int, y1Int));
                points.add(new Point(x2Int, y2Int));
                lines.add(new Line(c, thicknessInt, x1Int, y1Int, x2Int, y2Int));
            }
        }
        catch (FileNotFoundException e){
            System.out.println("Could not find file");
        } catch (IOException e){
            System.out.println("Invalid file format");
            e.printStackTrace();
        }
        rewind(numLines());
        unsavedProgress = false;
        setChanged();
        notifyObservers();
    }

    public void saveAsBinary(File file){
        try {
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(lines);
            out.close();
            fout.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        unsavedProgress = false;
    }

    public void loadBinary(File file){
        ArrayList<Line> deserializedLines = null;
        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            deserializedLines = (ArrayList<Line>) oin.readObject();
            oin.close();
            fin.close();
        } catch(IOException e) {
            e.printStackTrace();
            return;
        } catch(ClassNotFoundException c) {
            System.out.println("Line class not found");
            c.printStackTrace();
            return;
        }
        lines.clear();
        points.clear();
        for(int i = 0; i < deserializedLines.size(); i++){
            Line line = deserializedLines.get(i);
            int[] start = line.getStart();
            int[] end = line.getEnd();
            points.add(new Point(start[0], start[1]));
            points.add(new Point(end[0], end[1]));
            lines.add(new Line(line.getColour(), line.getThickness(), start[0], start[1], end[0], end[1]));
        }
        rewind(numLines());
        unsavedProgress = false;
        setChanged();
        notifyObservers();
    }

    public void newFile(){
        lines.clear();
        points.clear();
        unsavedProgress = false;
        setChanged();
        notifyObservers();
    }

    public boolean isUnsavedProgress(){
        return  unsavedProgress;
    }
}
