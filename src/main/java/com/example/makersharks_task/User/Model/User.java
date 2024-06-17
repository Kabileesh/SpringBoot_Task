package com.example.makersharks_task.User.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document("users")
@Data
@Setter
public class User {
    @Id
    private String _id;
    private String name;

    @Indexed(unique = true)
    private String username;
    private final String hash;

    private String password;

    public User(String username, String name, String hash){
        this.username = username;
        this.name = name;
        this.hash = hash;
    }

}
