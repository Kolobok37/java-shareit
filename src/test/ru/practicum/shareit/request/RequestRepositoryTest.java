package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {"db.name=test"})
class RequestRepositoryTest {
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    private void addRequest() {
        User user = new User(1L, "alex", "alex@gmail.com");
        User user2 = new User(2L, "alex2", "alex2@gmail.com");
        userRepository.save(user);
        userRepository.save(user2);

        Request request = new Request(null, "request1",
                LocalDateTime.now(), user, new ArrayList<>());
        Request request2 = new Request(null, "request2",
                LocalDateTime.now(), user, new ArrayList<>());
        Request request3 = new Request(null, "request3",
                LocalDateTime.now(), user2, new ArrayList<>());
        requestRepository.save(request);
        requestRepository.save(request2);
        requestRepository.save(request3);
    }

    @AfterEach
    private void deleteAll() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByUserId() {
        List<Request> requestList = requestRepository.findByUserId(1L);

        assertTrue(requestList.size() == 2);
    }
}