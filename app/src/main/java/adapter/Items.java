package adapter;

/**
 * To be used to load items for groups, users, topics
 */

public class Items {
    public String uid;
    private String name;

    public Items() {
    }

    public Items(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
