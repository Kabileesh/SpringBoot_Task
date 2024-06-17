package com.example.makersharks_task.User.Services;

import com.example.makersharks_task.Config.JwtGenerator;
import com.example.makersharks_task.ExceptionHandler.ApplicationException;
import com.example.makersharks_task.User.Model.User;
import com.example.makersharks_task.User.Repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtGenerator jwtGenerator;
    @Override
    public User createUser(User user) {
        if(userRepository.findByUsername(user.getUsername()) != null)
            throw new ApplicationException("Email already registered", "User already found", HttpStatus.BAD_REQUEST);
        return userRepository.save(user);
    }

    @Override
    public User getUser(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null)
            throw new ApplicationException("No user registered with the given email", "User not found", HttpStatus.NOT_FOUND);
        return user;
    }

    @Override
    public User getUserById(String _id){
        return userRepository.findBy_id(_id);
    }

    @NotNull
    private Map<?, ?> getMap(User user) {
        Map<String, String> userObj = new HashMap<>();
        userObj.put("_id", user.get_id());
        userObj.put("name", user.getName());
        userObj.put("username", user.getUsername());
        userObj.put("accessToken", jwtGenerator.generateToken(user));
        return userObj;
    }

    public Map<?,?> Login(String username, String password) throws ApplicationException {
        User user = getUser(username);
        if(passwordEncoder.matches(password, user.getHash())) {
            return getMap(user);
        }
        throw new ApplicationException("Email or password given is wrong", "Unauthenticated", HttpStatus.UNAUTHORIZED);
    }

    public Map<?, ?> Register(String username, String name, String password) throws ApplicationException{
        String hash = passwordEncoder.encode(password);
        User newUser = createUser(new User(username, name, hash));
        return getMap(newUser);
    }

    public Map<?, ?> GetUserDetails(String username, String reqUserEmail) throws ApplicationException {
        if(!username.equals(reqUserEmail)){
            throw new ApplicationException("UNAUTHORIZED", "Unauthorized access", HttpStatus.UNAUTHORIZED);
        }
        User user = getUser(username);
        Map<String, String> userObj = new HashMap<>();
        userObj.put("_id", user.get_id());
        userObj.put("name", user.getName());
        userObj.put("username", user.getUsername());
        return userObj;
    }
}
