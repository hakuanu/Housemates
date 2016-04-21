package stevenyoon.housemates;

/**
 * Created by StevenYoon on 4/20/16.
 */
public class GroupMember {
    private int id;
    private String name;
    private String balance;
    //private Group group;

    public GroupMember(int id, String name, String balance){
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
