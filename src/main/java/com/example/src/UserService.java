package com.example.src;

import com.example.annotation.*;

@Component
@Scope("prototype")
@Service
public class UserService implements UserServiceInterface {
    private final UserRepositoryImpl userRepositoryImpl;
    private final LoggerService loggerService;

    @Autowired
    public UserService(UserRepositoryImpl userRepositoryImpl, LoggerService loggerService) {
        this.userRepositoryImpl = userRepositoryImpl;
        this.loggerService = loggerService;
        this.loggerService.log("UserService created.");
    }

    public void processData() {
        String data = userRepositoryImpl.getData("1");
        System.out.println("UserService processing: " + data);
        loggerService.log("Data processed: " + data);
    }

    @PostConstruct
    public void startup() {
        loggerService.log("UserService started.");
    }

    @PreDestroy
    public void destroy() {
        loggerService.log("UserService is being destroyed.");
    }
}