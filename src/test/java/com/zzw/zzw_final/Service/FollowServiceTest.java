package com.zzw.zzw_final.Service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FollowServiceTest {

    @Test
    void follow() {
    }

    @Test
    void getFollow() {
    }

    @Test
    void getFollower() {
    }

    @Test
    void getOthersFollow() {
    }

    @Test
    void getOthersFollower() {
    }
}