package com.mukesh.userservice.Controller;

import com.mukesh.userservice.DTO.*;
import com.mukesh.userservice.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final  UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        return new ResponseEntity<>(userService.register(registerRequest), HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginrequest){
        return new ResponseEntity<>(userService.login(loginrequest),HttpStatus.OK);
    }
    @PostMapping("/forgotpassword")
    public ResponseEntity<Void> forgetpassword(@RequestBody ForgetPasswordRequest forgetpasswordRequest){
        userService.getOtp(forgetpasswordRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PostMapping("/resetpassword")
    public ResponseEntity<String> resetpassword(@RequestBody ResetPasswordRequest resetpasswordRequest){
        userService.resetpassword(resetpasswordRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/getuser")
    public ResponseEntity<UserResponse> GetuserByEmail(@RequestParam String email){
        if(email.contains("%40")){
            String newemail=email.replace("%40","@");
            return new ResponseEntity<>(userService.getuser(newemail),HttpStatus.OK);
        }else{

            return new ResponseEntity<>(userService.getuser(email),HttpStatus.OK);
        }
    }
}
