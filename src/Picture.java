public class Picture {

    private String sourceURL;
    private String title;
    private String fileType;


    public Picture() {
    }

    /**
     * returns the URL to the picture on the network
     * @return
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * stores the URL to the picture on the network
     * @param sourceURL
     */
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    /**
     * returns the given title of the picture
     * @return
     */
    public String getTitle() {
        if (title!=null)
            return title;
        else
            return "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
