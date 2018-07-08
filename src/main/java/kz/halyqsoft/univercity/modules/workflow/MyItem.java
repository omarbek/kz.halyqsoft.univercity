package kz.halyqsoft.univercity.modules.workflow;

import com.vaadin.data.Item;

public class MyItem {
    private Item item;
    private String currentId;
    private String parentId;

    public MyItem(Item item, String currentId , String parentId) {
        this.item = item;
        this.parentId = parentId;
        this.currentId = currentId;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
