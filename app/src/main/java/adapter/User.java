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
    private Boolean status_visible;
    private Boolean user_visible;
    private String devicetoken;


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
    public User(String devicetoken,String name, String email, String uid, String last_active, String ppic, String status, Boolean status_visible, Boolean user_visible) {
        this.devicetoken = devicetoken;
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.ppic = ppic;
        this.status = status;
        this.status_visible = status_visible;
        this.user_visible = user_visible;
        this.last_active = last_active;
    }

    public String getDevicetoken() {
        return devicetoken;
    }

    public void setDevicetoken(String devicetoken) {
        this.devicetoken = devicetoken;
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

    public Boolean getStatus_visible() {
        return status_visible;
    }

    public void setStatus_visible(Boolean status_visible) {
        this.status_visible = status_visible;
    }

    public Boolean getUser_visible() {
        return user_visible;
    }

    public void setUser_visible(Boolean user_visible) {
        this.user_visible = user_visible;
    }

    public String getLast_active() {
        return last_active;
    }

    public void setLast_active(String last_active) {
        this.last_active = last_active;
    }
}
