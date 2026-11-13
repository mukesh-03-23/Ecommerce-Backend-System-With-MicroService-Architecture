package com.mukesh.userservice.Service;
import com.mukesh.userservice.Client.NotificationClient;
import com.mukesh.userservice.DTO.*;
import com.mukesh.userservice.Entity.Otp;
import com.mukesh.userservice.Entity.Roles;
import com.mukesh.userservice.Entity.user;
import com.mukesh.userservice.Exceptions.UserExisted;
import com.mukesh.userservice.Exceptions.UserNotFound;
import com.mukesh.userservice.Exceptions.WrongCredentials;
import com.mukesh.userservice.Jwtutility.JwtServices;
import com.mukesh.userservice.Repository.OtpRepo;
import com.mukesh.userservice.Repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtServices  jwtServices;
    private final NotificationClient notificationClient;
    private final OtpRepo otprepo;
    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtServices jwtServices, NotificationClient notificationClient, OtpRepo otprepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtServices = jwtServices;
        this.notificationClient = notificationClient;
        this.otprepo = otprepo;
    }
    public String register(RegisterRequest registerRequest) {
        if (registerRequest == null || registerRequest.getPassword() == null
                || !registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        user user=userRepo.findByUseremail(registerRequest.getEmail()).orElse(null);
        if(user!=null){
            throw new UserExisted();
        }
        user=new  user();
        user.setUseremail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Roles.valueOf(registerRequest.getRole()));
        user.setUsername(registerRequest.getFullname());
        userRepo.save(user);
        return "user registerd successfully";
    }


    public String login(LoginRequest loginRequest) {
        user user=userRepo.findByUseremail(loginRequest.getEmail()).orElseThrow(()->new UserNotFound());
        if(passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            String token= jwtServices.GenerateToken(user);
            return token;
        }
        else{
            throw new WrongCredentials();
        }
    }

    @Transactional
    public void getOtp(ForgetPasswordRequest forgetpasswordRequest) {
        user user=userRepo.findByUseremail(forgetpasswordRequest.getEmail()).orElse(null);
        if(user==null)
        {
           throw new UserNotFound();
        }
        String otp=notificationClient.getOtp(forgetpasswordRequest);
        otprepo.deleteOtpByEmail(forgetpasswordRequest.getEmail());
        Otp dbotp=new Otp();
        dbotp.setEmail(forgetpasswordRequest.getEmail());
        dbotp.setOtp(otp);
        otprepo.save(dbotp);
    }
    @Transactional
    public String resetpassword(ResetPasswordRequest request){
        user user=userRepo.findByUseremail(request.getEmail()).orElse(null);
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new WrongCredentials();
        }
        if(user==null)
        {
            throw new UserNotFound();
        }
        String otp= request.getOtp();
        Otp dbotp=otprepo.getOtpByEmail(request.getEmail()).orElse(null);
        if(dbotp==null)
        {
            throw new WrongCredentials();
        }
        else if(dbotp.getExpiry() != null && dbotp.getExpiry().isAfter(LocalDateTime.now())
                && dbotp.getOtp().equals(otp)){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepo.save(user);
            otprepo.deleteOtpByEmail(request.getEmail());
            return "password reset successfully";
        }
        throw new WrongCredentials();
    }


    public UserResponse getuser(String email){
        user user=userRepo.findByUseremail(email).orElseThrow(()->new UserNotFound());
        UserResponse userResponse=new UserResponse();
        userResponse.setEmail(user.getUseremail());
        userResponse.setName(user.getUsername());
        return userResponse;
    }
}
