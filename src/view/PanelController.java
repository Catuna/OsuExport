package view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.ResourceBundle;

public class PanelController implements Initializable {
    //JavaFX UI injection variables
    @FXML private ListView<OsuFile> songList;
    @FXML private AnchorPane root;

    //Path to the folder last chosen by the user as the osu/songs folder
    String songsFolder;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<OsuFile> songs =  FXCollections.observableArrayList();

        //Callback for connecting listView checkboxes to the OsuFile entries
        Callback<OsuFile, ObservableValue<Boolean>> callback = new Callback<OsuFile, ObservableValue<Boolean>>() {
            @Override
            public BooleanProperty call(OsuFile file) {
                return file.selected;
            }
        };

        songList.setCellFactory(CheckBoxListCell.forListView(callback));
        songList.setItems(FXCollections.observableArrayList(songs));
    }

    public void doOpenSongs() {
        //Open folder select dialog and get the path to the songs folder
        DirectoryChooser directoryDialog = new DirectoryChooser();
        directoryDialog.setTitle("Select your songs folder");

        File songsPath = directoryDialog.showDialog(getStage());

        if(songsPath != null) {
            generateSongStructure(songsPath);
            songsFolder = songsPath.getAbsolutePath();
        }
    }

    public void doExit() {
        getStage().close();
    }

    //Iterate the songs folder and generate new songs structure
    private void generateSongStructure(File songsDirectory) {
        songList.getItems().clear();

        for(File songFolder : songsDirectory.listFiles()) {
            //Check that only directories are processed
            if(songFolder.isDirectory() || songFolder.listFiles() == null) {
            	if(!songFolder.canRead()) {
            		InfoDialog errorDiag = new InfoDialog("Could not read from chosen folder");
                    errorDiag.show();
                    return;
            	}
                //Find .osu files in folder and process them
            	try {
	                for(File osuFile : songFolder.listFiles()) {
	                    int indexOfExtension = osuFile.getName().lastIndexOf('.');
	                    if(indexOfExtension == -1)
	                        continue;
	                    String extension = osuFile.getName().substring(indexOfExtension);
	                    if(extension.equals(".osu")) {
	                        songList.getItems().add(new OsuFile(osuFile));
	                        break; //We only need one .osu file per song folder
	                    }
	                }
            	}
            	catch(Exception e) {
            		InfoDialog errorDiag = new InfoDialog("Could not read from chosen folder");
                    errorDiag.show();
                    return;
            	}
            }
        }
        //Check that there was any songs found at all
       if(songList.getItems().isEmpty()) {
           InfoDialog errorDiag = new InfoDialog("Could not locate any songs, are you sure you chose the osu/songs folder?");
           errorDiag.show();
       }
        else {
           //Entries on the list should be compared alphabetically by title
           Comparator<OsuFile> compare = new Comparator<OsuFile>() {
               @Override
               public int compare(OsuFile o1, OsuFile o2) {
                   return o1.meta.getSongname().compareTo(o2.meta.getSongname());
                   }
           };
           FXCollections.sort(songList.getItems(), compare);
       }
    }

    public void doExportSongs() {
        //Check that there is something to export
        if(songList.getItems().isEmpty()) {
            InfoDialog errorDiag = new InfoDialog("Please use \"file->Open songs folder\" before exporting songs");
            errorDiag.show();
            return;
        }
        boolean oneSelected = false;
        for(Object raw : songList.getItems()) {
            if(((OsuFile)raw).selected.get()) {
                oneSelected = true;
            }
        }
        if(!oneSelected) {
            InfoDialog errorDiag = new InfoDialog("Please select those songs you wish to export");
            errorDiag.show();
            return;
        }

        //Open folder select dialog and get the path to the output folder
        DirectoryChooser directoryDialog = new DirectoryChooser();
        directoryDialog.setTitle("Select output folder");

        Path outputPath = directoryDialog.showDialog(getStage()).toPath();
        
        int exportCount = 0;
        boolean exportSuccess = true;
        //TODO: Display a progress bar of this process
        for(Object raw : songList.getItems()) {
            OsuFile song = (OsuFile)raw;
            File outputFile = new File(outputPath.resolve(song.meta.getSongname()).toString().concat(".mp3"));
            File inputFile = song.audioFile;
            if(song.selected.get()) {
                try {
                    //Create file if it doesn't exist
                    if(!outputFile.exists())
                        Files.createFile(outputFile.toPath());
                    //Copy the content from the songs folder to the output folder
                    Files.copy(inputFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    exportCount++;
                }
                catch(AccessDeniedException e) {
                	exportSuccess = false;
                    InfoDialog errorDiag = new InfoDialog("Failed to copy files to output directory: Access denied");
                    errorDiag.show();
                    break;
                }
                catch(NoSuchFileException e) {
                	exportSuccess = false;
                    InfoDialog errorDiag = new InfoDialog(
                    		"Failed to copy files to output directory: Failed to locate file:\n" +
                    		e.getFile());
                    errorDiag.show();
                    break;
                }
                catch(IOException e) {
                	exportSuccess = false;
                    InfoDialog errorDiag = new InfoDialog(
                    		"Failed to copy files to output directory: " + e.getClass().getName());
                    errorDiag.show();
                    break;
                }
            }
        }

        if(exportSuccess) {
        	InfoDialog infoDiag = new InfoDialog(
	        		"Successfully exported " + exportCount + " songs to the output directory!" +
	        		"\nOutput directory: " + outputPath.toString());
	        infoDiag.show();
        }
    }
    public void doSelectAll() {
        for(Object raw : songList.getItems()) {
            ((OsuFile)raw).selected.setValue(true);
        }
    }

    public void doDeselectAll() {
        for(Object raw : songList.getItems()) {
            ((OsuFile)raw).selected.setValue(false);
        }
    }

    public void doDisplayAbout() {
        InfoDialog errorDiag = new InfoDialog(
                "Created by: Catuna" +
                "\nContact me by mail: chrbarrol@gmail.com" +
                "\nThis project is open source at: github.com/Catuna/OsuExport");
        errorDiag.show();
    }


    //Used to get the current stage of root element, we cannot do this at initialization because the stage hasn't been set by then
    private Stage getStage() {
        return (Stage)root.getScene().getWindow();
    }
}
