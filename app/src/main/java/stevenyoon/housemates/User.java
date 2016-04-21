package stevenyoon.housemates;

/**
 * Created by austin on 4/21/16.
 */
public class User {
    private String name;
    private int status;
    private String id;

    public User() {
        this.name = null;
        this.status = 0;
    }

    public User(String name, int status, String id) {
        super();
        this.name = name;
        this.status = status;
        this.id = id;
    }
    public User(String name, int status) {
        super();
        this.name = name;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void changeStatus() {
        if(this.status == 0) {
            this.status = 1;
        }
        else {
            this.status = 0;
        }
    }
}
