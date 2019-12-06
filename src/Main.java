import java.io.*;
import java.net.URL;
import java.text.ParseException;

public class Main {

    String inputFile;
    String outputFile;
    boolean downloadMode;
    public static int downloadCounter;

    public static void main(String[] args) {
        Main main = new Main();

        if (args.length<=2){
            showUsage();
        }

        try{
            main.setInputFile(args[0]);
            main.setOutputFile(args[1]);
            main.setDownloadMode(Boolean.parseBoolean(args[2]));
            downloadCounter=0;
        } catch (Exception e){
            showUsage();
        }
        main.readFileByLine();

    }

    public static void showUsage(){
        System.out.println("Due to the lack of the option to download saved pictures by the stored name from the MyHeritage Website, this little HelperTool was created by");
        System.out.println("(c) 2019 Stefan Kutschera, BSc MSc");
        System.out.println("");

        System.out.println("Usage: java -jar MHPictureDownloader.jar <input GEDCOM file path> <output path> <boolean download mode>" );
        System.out.println("" +
                "\t<input GEDCOM file path> : define the full path to your input GEDCOM file\n" +
                "\t<output path> : define the full path were your photos should be saved (Folder will be created if not existent)\n" +
                "\t<boolean download mode> : true --> Photos will be downloaded false --> See how the program would behave, no files will be downloaded\n");
        System.out.println("EXAMPLE: java -jar MHPictureDownloader.jar C:\\\\temp\\gedcom_testfile.txt C:\\\\temp\\pics true");
        System.exit(0);
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public void setDownloadMode(boolean downloadMode) {
        this.downloadMode = downloadMode;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    /**
     * Form of MyHeritage GEDCOM filestructure:
     * 3 FORM jpg
     * 3 FILE https://www.myheritageimages.com/D/storage/someSite/some/more/directories/0000001_123456.jpg
     * 3 TITL NameOfPictureHowItWasUploadedAndIsShownInHyHeritage
     *
     */
    private void readFileByLine(){
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            String id="error";
            while ((line = br.readLine()) != null) {
                if(line.startsWith("0 ")){
                    String[] possibleIdLine = line.split("@");
                    if (possibleIdLine.length>1){
                        line = br.readLine();
                        if(line.contains("NAME")){
                            id=(possibleIdLine[1]+"_"+line.split("NAME ")[1]).replace("/","").replace(" ","_");
                        }
                    }
                }
                if(line.contains("FORM ")) {
                    String rawFileType = line;
                    String rawfileUrl = br.readLine();
                    String rawFileTitle = br.readLine();
                    extractFileMetaData(id,rawFileType,rawfileUrl,rawFileTitle);
                }
            }
            setOutputFile("---------- FINISHED ---------------------");
            System.out.println("Downloaded: " + downloadCounter + " Files");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractFileMetaData(String fileId, String rawFileType, String rawFileUrl, String rawFileTitle){
        String fileType = rawFileType.split(" ")[rawFileType.split(" ").length-1];
        String fileUrl = rawFileUrl.split(" ")[rawFileUrl.split(" ").length-1];
        String fileTitle = rawFileTitle.split(" ")[rawFileTitle.split(" ").length-1];
        try {
            downloadPicture(fileId,fileUrl,fileTitle,fileType,downloadMode);
            downloadCounter++;
        } catch (IOException e) {
            System.out.println("ERROR while downloading: " + fileUrl + " --> SAVE AS: " + "C:\\\\temp\\pics\\"+fileId+"_"+fileTitle+"."+fileType );
            e.printStackTrace();
        }
    }

    private void downloadPicture(String fileId,String fileUrl, String fileTitle, String fileType,boolean downloadMode) throws IOException {
        if(downloadMode){
            URL url = new URL(fileUrl);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            File directory = new File("C:\\\\temp\\pics\\");
            if (! directory.exists()){
                directory.mkdirs();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
            //Save image
            FileOutputStream fos = new FileOutputStream(directory+"\\"+fileId+"_"+fileTitle+"."+fileType);
            fos.write(response);
            fos.close();
        }
        System.out.println("Downloaded: " + fileUrl + " --> SAVE AS: " + "C:\\\\temp\\pics\\"+fileId+"_"+fileTitle+"."+fileType );
    }
}
