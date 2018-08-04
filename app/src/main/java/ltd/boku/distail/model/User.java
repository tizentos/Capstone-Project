package ltd.boku.distail.model;

import java.util.List;

public class User {
    private String userId;
    //private Cart cart;    TODO cart should have a node with id=userId
    private String role;

    public User() {
    }

    public User(String userId, String role) {
        this.userId = userId;
//        this.cart = cart;
        this.role = role;
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
}
