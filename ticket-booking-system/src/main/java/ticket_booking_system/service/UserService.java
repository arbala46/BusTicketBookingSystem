package ticket_booking_system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ticket_booking_system.dto.UserResponseDTO;
import ticket_booking_system.entity.User;
import ticket_booking_system.exception.InvalidCredentialsException;
import ticket_booking_system.exception.UserAlreadyExistsException;
import ticket_booking_system.exception.UserNotFoundException;
import ticket_booking_system.repository.userRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    userRepository userRepository;

    @Autowired
    BCryptPasswordEncoder BCryptPasswordEncoder;

    public User createUser(User user)
    {
        boolean existEmail = userRepository.existsByEmail(user.getEmail());

        if(existEmail){
            log.warn("User already exists: {}", user.getEmail());
            throw new UserAlreadyExistsException("User Already Exist");
        }
            String EncryptedPassword = BCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(EncryptedPassword);
            User savedUser = userRepository.save(user);
            log.info("User Saved Successfully!");
            return savedUser;
    }

    public User loginUser(User user)
    {

        User loggedUser = userRepository.findByEmail(user.getEmail()).
                orElseThrow(()-> {log.warn("User not found:{}",user.getEmail());
                       return new UserNotFoundException("User not registered");
                });


        String hashedPassword = loggedUser.getPassword();
        boolean passwordChk = BCryptPasswordEncoder.matches(user.getPassword(),hashedPassword);

        if(passwordChk)
        {
            log.info("User logged in Successfully!");
            return loggedUser;
        }
        log.info("Invalid Credentials");
        throw new InvalidCredentialsException("Invalid Credentials");
    }

    public List<UserResponseDTO> UsersList()
    {
        List<User> userList = userRepository.findAll();
        List<UserResponseDTO> userResponse = new ArrayList<>();

        for(User user : userList)
        {
            UserResponseDTO responseDTO = new UserResponseDTO();
            responseDTO.setName(user.getName());
            responseDTO.setEmail(user.getEmail());
            responseDTO.setGender(user.getGender());
            userResponse.add(responseDTO);
        }
        return userResponse;
    }

}
