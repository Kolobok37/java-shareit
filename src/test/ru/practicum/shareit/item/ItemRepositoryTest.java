package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
@TestPropertySource(properties = { "db.name=test"})
@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    private void addItem(){
        User user = new User(null, "alex", "alex@gmail.com");
        User user2 = new User(null, "alex2", "alex2@gmail.com");
        userRepository.save(user);
        userRepository.save(user2);

        Item item = new Item(null, user, "itemOne",
                "itemIsGood", true, new ArrayList<>(), null, null, null);
        Item item2 = new Item(null, user, "itemTwo",
                "itemIsGood2", true, new ArrayList<>(), null,null, null);
        Item item3 = new Item(null, user2, "itemOneThree",
                "itemGood3", false, new ArrayList<>(), null,null, null);

        itemRepository.save(item);
        itemRepository.save(item2);
        itemRepository.save(item3);
    }

    @AfterEach
    private void deleteAll(){
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void findByOwnerId() {
        List<Item> itemList = itemRepository.findByOwnerId(1L);

        assertTrue(itemList.size()==2);
    }

    @Test
    void findByNameContainingIgnoreCase() {
        List<Item> itemList = itemRepository.findByNameContainingIgnoreCase("one");

        assertTrue(itemList.size()==2);
    }

    @Test
    void findByDescriptionContainingIgnoreCase() {
        List<Item> itemList = itemRepository.findByDescriptionContainingIgnoreCase("good3");

        assertTrue(itemList.size()==1);
    }
}