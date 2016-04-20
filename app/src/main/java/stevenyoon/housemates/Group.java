package stevenyoon.housemates;

import java.util.ArrayList;

/**
 * Created by StevenYoon on 4/20/16.
 */
public class Group {
    private int id;
    private String name;
    private ArrayList<GroupMember> members;

    public Group (int id, String name) {
        this.id = id;
        this.name = name;
        members = new ArrayList<GroupMember>();
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

    public ArrayList<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<GroupMember> members) {
        this.members = members;
    }

    public void addMember(GroupMember g){
        members.add(g);
    }

}
