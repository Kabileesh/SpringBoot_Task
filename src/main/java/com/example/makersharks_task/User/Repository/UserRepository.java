package com.example.makersharks_task.User.Repository;

import com.example.makersharks_task.User.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findBy_id(String _id);
    User findByUsername(String username);
}
