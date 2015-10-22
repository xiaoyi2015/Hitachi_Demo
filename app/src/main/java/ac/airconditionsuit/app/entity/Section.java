package ac.airconditionsuit.app.entity;

import com.google.gson.Gson;

import java.util.ArrayList;
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
        if(pages == null){
            pages = new ArrayList<>();
        }
        return pages;
    }

    public void setPages(List<Room> pages) {
        this.pages = pages;
    }

    public static Section getSectionFromJsonString(String section) {
        if (section == null) {
            return null;
        }
        return new Gson().fromJson(section, Section.class);
    }
}