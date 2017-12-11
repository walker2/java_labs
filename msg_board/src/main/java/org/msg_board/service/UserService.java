package org.msg_board.service;

import org.msg_board.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserService {

    private static Logger logger = Logger.getLogger(UserService.class.getName());
    private List<User> users;

    public UserService() {
        users = new ArrayList<>();
    }

    public void loadFromFile(String fileName) {
        try (
                FileReader reader = new FileReader(fileName);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String string;
            while ((string = br.readLine()) != null) {
                String[] tmp = string.split(":");
                String name = tmp[0];
                String password = tmp[1];
                users.add(new User(name, password));
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public void addUser(String name, String password) {
        users.add(new User(name, password));
    }
    public void saveToFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            for (User usr : users) {
                writer.write(usr.getName() + ":" + usr.getPassword() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
    }

    public boolean verify(String name, String password) {
        User user = getByName(name);
        return user != null && user.getPassword().equals(password);
    }

    public void deleteByName(String name) {
        users.remove(getByName(name));
    }
    private User getByName(String name) {
        return users.stream()
                .filter(p -> p.getName()
                        .equals(name))
                .findFirst().orElse(null);
    }

    public List<User> getUsers() { return users; }
}
