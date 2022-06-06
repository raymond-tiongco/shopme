package com.shopme.shopmebackend.admin.service;

import com.shopme.shopmebackend.admin.entity.User;

import org.junit.Assert;
import org.junit.Test;


public class UserServiceImplTest {


    @Test
    public void ParseUserRecord(){
        String input = "Vince%!Strix%!vince@gmail.com";
        String delimiter = "%!";
        String[] token = input.split(delimiter);
        User user = new User();

        user.setFirstName(token[0]);
        user.setLastName(token[1]);
        user.setEmail(token[2]);

        Assert.assertEquals("Vince", user.getFirstName());
        Assert.assertEquals("Strix",user.getLastName());
        Assert.assertEquals("vince@gmail.com",user.getEmail());
    }

}
