package org.msg_board.model;

public class User {

    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public  void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public  void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object object) {
        if(this == object)
            return true;
        if(object == null || this.getClass() != object.getClass())
            return false;
        User user = (User) object;
        return this.name.equals(user.name);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    public String toString() {
        return name;
    }
}
