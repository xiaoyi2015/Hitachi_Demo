package ac.airconditionsuit.app.entity;

/**
 * Created by ac on 5/26/15.
 * the entity for Home
 */
public class Home extends RootEntity{
    private String name;
    String filename;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
