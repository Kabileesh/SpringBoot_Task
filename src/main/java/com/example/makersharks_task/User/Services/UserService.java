package com.example.makersharks_task.User.Services;

import com.example.makersharks_task.User.Model.User;

public interface UserService {
    public User createUser(User user);
    public User getUser(String string);
    public User getUserById(String _id);
}
