package view;

//Class to keep track of metadata associated with the OsuFile
public class Meta {
    public String title, artist, creator, version, source;

    public Meta() {
        this.title = "";
        this.artist = "";
        this.creator = "";
        this.version = "";
        this.source = "";
    }

    public Meta(String title, String artist, String creator, String version, String source) {
        this.title = title;
        this.artist = artist;
        this.creator = creator;
        this.version = version;
        this.source = source;
    }

    //Returns true if none of the meta fields are empty
    public boolean metaValid() {
        return !(this.title.isEmpty() ||
                this.artist.isEmpty() ||
                this.creator.isEmpty() ||
                this.version.isEmpty() ||
                this.source.isEmpty());
    }

    //Returns the formatted name of this song
    public String getSongname() {
        return (artist + " - " + title);
    }
}
