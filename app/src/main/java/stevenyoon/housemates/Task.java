package stevenyoon.housemates;

/**
 * Created by austinha on 3/16/16.
 */
public class Task {
    private String description;
    private int status;
    private int id;

    public Task() {
        this.description = null;
        this.status = 0;
    }

    public Task(String description, int status) {
        super();
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
