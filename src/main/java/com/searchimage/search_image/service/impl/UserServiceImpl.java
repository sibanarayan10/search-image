package com.searchimage.search_image.service.impl;
import com.searchimage.search_image.dto.LoginRequestDto;
import com.searchimage.search_image.dto.UserDetailResponseDto;
import com.searchimage.search_image.dto.UserDto;
import com.searchimage.search_image.entity.User;
import com.searchimage.search_image.entity.enums.RecordStatus;
import com.searchimage.search_image.exception.EntityAlreadyExistException;
import com.searchimage.search_image.repository.FollowRepository;
import com.searchimage.search_image.repository.UserRepository;
import com.searchimage.search_image.security.UserPrincipal;
import com.searchimage.search_image.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final  FollowRepository followRepository;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
                           PasswordEncoder passwordEncoder,FollowRepository followRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.followRepository=followRepository;

    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found with userId: " + userId)
                );
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found with email: " + email)
                );
    }

    @Override
    public User findUserByName(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found with name: " + username)
                );
    }

    @Override
    public boolean userExists(String username, String email) {
        Optional<User> byUsername = this.userRepository.findByName(username);
        Optional<User> byEmail = this.userRepository.findByEmail(email);
        return byUsername.isPresent() || byEmail.isPresent();
    }

    @Override
    public void saveUserWithUpdatedPassword(User userEntity) {
        this.userRepository.save(userEntity);
    }

    public void registerUser(UserDto user){
        String email=user.getEmail();
        if(userRepository.findByEmail(email).isPresent()){
            throw new EntityAlreadyExistException("User already exist");
        }
        User u=modelMapper.map(user,User.class);
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(u);
    }

    public Long loginUser(LoginRequestDto request){
            String email=request.getEmail();
            Optional<User> user=this.userRepository.findByEmail(email);
            if(user.isPresent()){
                User currUser=user.get();
                String password=currUser.getPassword();
                String rqPassword= request.getPassword();
                boolean isMatched= this.passwordEncoder.matches(rqPassword,password);
                if(isMatched){
                    return currUser.getId();
                }
            }

            return -1L;
    }
    @Override
    public boolean deleteUser(Long id) {
        User u=findUserById(id);
        u.setRecordStatus(RecordStatus.DELETED);
        return true;

    }

    @Override
    public UserDetailResponseDto getUserDetail() {
       UserPrincipal currUser=getCurrentUser();
       Long userId=currUser.getUserId();
       User u=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User not found"));
       Long followerCount=followRepository.countByFollowingIdAndIsActiveTrue(userId);
       Long followingCount= followRepository.countByFollowedByIdAndIsActiveTrue(userId);
       UserDetailResponseDto dto=new UserDetailResponseDto();
       dto.setName(u.getName());
       dto.setEmail(u.getEmail());
       dto.setFollowerCount(followerCount);
       dto.setFollowingCount(followingCount);
       dto.setImgUrl(u.getImgUrl());
       return dto;



    }
    private UserPrincipal getCurrentUser(){
        return (UserPrincipal) SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();
    }
}
