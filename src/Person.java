import java.util.ArrayList;

public class Person {

    private final String ID;
    private String givenname; // Vorname
    private String surname; // Nachname
    private String name;
    private ArrayList<Picture> pictureList;


    public Person(String id) {
        ID = id;
        pictureList = new ArrayList<Picture>();
    }

    public String getID() {
        return ID;
    }

    public void addPicture(Picture pic){
        pictureList.add(pic);
    }

    public ArrayList getPictureList(){
        return pictureList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGivenname() {
        return givenname;
    }

    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


}
