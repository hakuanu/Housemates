package stevenyoon.housemates;

/**
 * Created by austinha on 3/16/16.
 */
public class Task {
    private String description;
    private int status;
    private String id;

    public Task() {
        this.description = null;
        this.status = 0;
    }

    public Task(String description, int status, String id) {
        super();
        this.description = description;
        this.status = status;
        this.id = id;
    }
    public Task(String description, int status) {
        super();
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setTask(String description) {
        this.description = description;
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
