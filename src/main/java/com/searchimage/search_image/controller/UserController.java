package com.searchimage.search_image.controller;

import com.searchimage.search_image.dto.LoginRequestDto;
import com.searchimage.search_image.dto.LoginResponseDto;
import com.searchimage.search_image.dto.UserDto;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.filter.JwtFilter;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.service.UserService;
import com.searchimage.search_image.utility.JWTUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtil;
    private final JwtFilter jwtFilter;

    @Autowired
    public UserController(UserService userService,UserRepository userRepository,ModelMapper modelMapper,PasswordEncoder passwordEncoder,JWTUtility jwtUtil,JwtFilter jwtFilter){
        this.userRepository=userRepository;
        this.userService=userService;
        this.modelMapper=modelMapper;
        this.passwordEncoder=passwordEncoder;
        this.jwtUtil=jwtUtil;
        this.jwtFilter=jwtFilter;
    }

    @PostMapping("api/auth/register")
    private ResponseEntity<?> register(@RequestBody UserDto userRequest){
        userService.registerUser(userRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }
    @PostMapping("api/auth/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse response
    ) {
        User user = userService.findUserByEmail(request.getEmail());
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );
        if (!passwordMatches) {
            return ResponseEntity
                    .status(401)
                    .body(new LoginResponseDto("Invalid credentials"));
        }
        String token = jwtUtil.generateToken(user.getEmail(),user.getId());
        Cookie cookie = new Cookie("AUTH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        return ResponseEntity.ok(
                new LoginResponseDto("Login successful")
        );
    }


    @PostMapping("api/user/log-out")
    public ResponseEntity<Boolean>logout(HttpServletRequest request,HttpServletResponse response){

        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(true);
    }

    @PutMapping("api/user/delete")
    public ResponseEntity<Boolean>deleteUser(HttpServletRequest request){
        String token=jwtFilter.extractTokenFromCookie(request);
        if(token!=null){
            String email=jwtUtil.getEmail(token);
            if(email!=null) {
                User u = userService.findUserByEmail(email);
                boolean result=userService.deleteUser(u.getId());
                if(result){
                    return ResponseEntity.ok(true);
                }
            }
        }
        return ResponseEntity.ok(false);
    }

    //    @GetMapping("api/users/:userId/details")
//    public ResponseEntity<UserDto> getUserDetail(@PathVariable Long userId){
//        User u= userService.findUserById(userId);
//
//        };
}
