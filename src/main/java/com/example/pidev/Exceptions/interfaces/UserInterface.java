package com.example.pidev.Exceptions.interfaces;


import com.example.pidev.Exceptions.*;
import com.example.pidev.models.User;

import java.util.List;

public interface UserInterface {
    void addUser(User p) throws EmptyFieldException, InvalidEmailException, IncorrectPasswordException, InvalidPhoneNumberException;
    void updateUser(User p) throws EmptyFieldException,InvalidPhoneNumberException, InvalidEmailException, IncorrectPasswordException, UserNotFoundException;
    void deleteUser(int id) throws UserNotFoundException;

    List<User> getUsers();

    User getUserbyID(int id) throws UserNotFoundException;
    User getUserbyEmail(String email) throws UserNotFoundException;
}
