package ac.airconditionsuit.app.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/9/25.
 */
public class Section extends RootEntity{
    String name;
    List<Room> pages;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getPages() {
        return pages;
    }

    public void setPages(List<Room> pages) {
        this.pages = pages;
    }
}