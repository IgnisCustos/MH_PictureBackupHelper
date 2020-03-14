import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    String inputFile;
    String outputDir;
    boolean downloadMode;
    public static int downloadCounter;
    private static ArrayList<Person> personArrayList = new ArrayList<Person>();

    public static void main(String[] args) {
        Main main = new Main();


        if (args.length<=2){
            showUsage();
        }

        try{
            main.setInputFile(args[0]);
            main.setOutputDir(args[1]);
            if (args.length<=1)
                main.setDownloadMode(false);
            else
                main.setDownloadMode(Boolean.parseBoolean(args[2]));
            downloadCounter=0;
        } catch (Exception e){
            showUsage();
        }
        main.readFileByLine();
        main.showAllData();

    }

    private void showAllData(){
        for (Person person : personArrayList) {
            System.out.println("ID " + person.getID());
            System.out.println("Name " + person.getName());
            System.out.println("GIVN " + person.getGivenname());
            System.out.println("SURN " + person.getSurname());
            ArrayList<Picture> pictureList = person.getPictureList();
            int pictureCount=0;
            for (Picture picture : pictureList){
                System.out.println("\t TITL " + picture.getTitle());
                String title = picture.getTitle();
                // TODO errorhandlung nullcheck and so on
                System.out.println("\t URL " + picture.getSourceURL());
                System.out.println("\t TYPE " + picture.getFileType());
                System.out.println("");
                try {
                    downloadPicture(person.getID(),picture.getSourceURL(),picture.getTitle(),picture.getFileType(),downloadMode);
                } catch (IOException e) {
                    System.out.println("Error wile downloading ID" + person.getID() +" :: URL" + picture.getSourceURL() );
                    e.printStackTrace();
                }
                pictureCount++;
            }
            System.out.println("-----------------------------------");
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

            File directory = new File(outputDir);
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
        System.out.println("Downloaded: " + fileUrl + " --> SAVE AS: " + outputDir+"\\"+fileId+"_"+fileTitle+"."+fileType );
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

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
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
            Person person = null;
            Picture picture = null;
            while ((line = br.readLine()) != null) {
                if(line.contains("INDI")){
                    String[] idLine = line.split("@");
                    person = new Person(idLine[1]);
                    personArrayList.add(person);
                    line = br.readLine();
                }
                if(line.contains("NAME") && person != null){
                    person.setName((line.split("NAME ")[1]).replace("/","").replace(" ","_"));
                }
                if(line.contains("FORM ") && person != null) {
                    picture = new Picture();
                    person.addPicture(picture);
                    picture.setFileType(line.split("FORM ")[1]);
                }
                if (line.contains("GIVN ")){
                    person.setGivenname(line.split("GIVN ")[1]);
                } else if (line.contains("SURN ")){
                    person.setSurname(line.split("SURN ")[1]);
                } else if (line.contains("FILE ") && picture!=null){
                    picture.setSourceURL(line.split("FILE ")[1]);
                } else if (line.contains("TITL ")  && picture!=null){
                    picture.setTitle(line.split("TITL ")[1]);
                }
            }
            System.out.println("---------- FINISHED ---------------------");
            System.out.println("Collected all data: " + downloadCounter + " Pictures");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
