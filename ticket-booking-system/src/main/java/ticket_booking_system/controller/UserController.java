package ticket_booking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ticket_booking_system.dto.LoginResponseDTO;
import ticket_booking_system.entity.User;
import ticket_booking_system.service.JwtService;
import ticket_booking_system.service.UserService;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    JwtService jwtService;

    @PostMapping("/users/Create")
    public ResponseEntity<User> createUser(@RequestBody User user)
    {
        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping("/users/login")
        public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody User user)
    {
        User loggedUser = userService.loginUser(user);
        String token = jwtService.generateToken(loggedUser.getEmail());
        return ResponseEntity.ok(new LoginResponseDTO(token));

    }

    @GetMapping("/users/getUsersList")
    public void usersList()
    {
        System.out.println("It's Working");
    }

}
