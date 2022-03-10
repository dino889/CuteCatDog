package com.cutecatdog.controller;

import org.springframework.web.bind.annotation.PostMapping;

// import java.util.HashMap;
// import java.util.Map;

// import com.cutecatdog.model.UserDto;
// import com.cutecatdog.service.UserService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
// import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/users")
@Api("회원 관리")
public class UserController {
    
    @ApiOperation(value="test", notes="")
    @PostMapping
    public String test(){
        return "Hello";
    }

}
