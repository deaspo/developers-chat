package adapter;

/**
 * Created by polyc on 03/02/2017.
 */

public class User {
    public String last_active;
    private String name;
    private String email;
    private String uid;
    private String ppic;
    private String status;
    private String status_visible;
    private String user_visible;


    /**
     * Required public constructor
     */
    public User() {
    }

    /**
     * Use this constructor to create new User.
     * Takes user name, email and timestampJoined as params
     *
     * @param name
     * @param email
     * @param uid
     */
    public User(String name, String email, String uid, String last_active, String ppic, String status, String status_visible, String user_visible) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.ppic = ppic;
        this.status = status;
        this.status_visible = status_visible;
        this.user_visible = user_visible;
        this.last_active = last_active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate_joined() {
        return last_active;
    }

    public String getPpic() {
        return ppic;
    }

    public void setPpic(String ppic) {
        this.ppic = ppic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus_visible() {
        return status_visible;
    }

    public void setStatus_visible(String status_visible) {
        this.status_visible = status_visible;
    }

    public String getUser_visible() {
        return user_visible;
    }

    public void setUser_visible(String user_visible) {
        this.user_visible = user_visible;
    }

    public String getLast_active() {
        return last_active;
    }

    public void setLast_active(String last_active) {
        this.last_active = last_active;
    }
}
