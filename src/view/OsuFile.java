package view;

import javafx.beans.property.SimpleBooleanProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//Class to represent an .osu file as a ListView element
public class OsuFile{
    File audioFile;
    Meta meta;

    //Keeps track of whether the item is selected or not in the ListView
    public SimpleBooleanProperty selected;

    public OsuFile() {
        audioFile = null;
        meta = new Meta();

        selected = new SimpleBooleanProperty(false);
    }

    //Overriding toString is important because it's used by the CellFactory to choose what text is displayed for the OsuFile
    @Override
    public String toString() {
        return meta.getSongname();
    }

    //Constructor to make OsuFile from .osu file
    public OsuFile(File osuFile) {
        this();
        try {
            //TODO: This method of getting metadata might be inefficient, recreate with better optimization
            BufferedReader reader = new BufferedReader(new FileReader(osuFile));
            String nextLine;
            while((nextLine = reader.readLine()) != null) {
                if(nextLine.contains("AudioFilename:")) {
                    String audioFilename = nextLine.split(":")[1].trim(); //TODO: Check for relative or absolute path
                    audioFile = new File(osuFile.getParentFile().getAbsolutePath() + "\\" + audioFilename);
                }
                else if(nextLine.contains("Title:")) {
                    this.meta.title = getValueOfLine(nextLine);
                }
                else if(nextLine.contains("Artist:")) {
                    this.meta.artist = getValueOfLine(nextLine);
                }
                else if(nextLine.contains("Creator:")) {
                    this.meta.creator = getValueOfLine(nextLine);
                }
                else if(nextLine.contains("Version:")) {
                    this.meta.version = getValueOfLine(nextLine);
                }
                else if(nextLine.contains("Source:")) {
                    this.meta.source = getValueOfLine(nextLine);
                }
                
                if(this.meta.metaValid()) break;
            }
            reader.close();
        }
        
        catch (IOException e) {
            //TODO: Report this exception to the user
        }
    }

    //Attempts to find the key in the given line and returns it's value
    private String getValueOfLine(String line) {
        String[] split = line.split(":");
        if(split.length == 2)
            return split[1];
        else
            return "";
    }
}

