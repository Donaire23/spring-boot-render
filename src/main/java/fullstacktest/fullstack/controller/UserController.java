package fullstacktest.fullstack.controller;


import fullstacktest.fullstack.jwtConfiguration.JwtTokenUtil;
import fullstacktest.fullstack.jwtConfiguration.JwtUserDetailService;
import fullstacktest.fullstack.model.User;
import fullstacktest.fullstack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import org.springframework.http.HttpStatus;

@CrossOrigin
@RestController
@RequestMapping("/register")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUserDetailService jwtUserDetailService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User newUser) {

        PasswordEncoder passwordEncoder = User.getPasswordEncoder();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        userRepository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User loginUser) {

        List<User> userList = userRepository.findByEmail(loginUser.getEmail());

        if (!userList.isEmpty()) {
            User storedUser = userList.get(0);

            PasswordEncoder passwordEncoder = User.getPasswordEncoder();
            if (passwordEncoder.matches(loginUser.getPassword(), storedUser.getPassword())) {

                UserDetails userDetails = jwtUserDetailService.loadUserByUsername(storedUser.getEmail());
                String jwtToken = jwtTokenUtil.generateToken(String.valueOf(userDetails));

                return ResponseEntity.ok(jwtToken);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getData")
    List<User> getAllUsers() {
    return  userRepository.findAll();
    }



}
