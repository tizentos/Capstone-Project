package ltd.boku.distail.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String userId;
    //private Cart cart;    TODO cart should have a node with id=userId
    private String role;
    private String userName;

    @Exclude
    private static final long serialVersionUID=2L;

    public User() {
    }

    public User(String userId, String role, String userName) {
        this.userId = userId;
//        this.cart = cart;
        this.role = role;
        this.userName= userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
