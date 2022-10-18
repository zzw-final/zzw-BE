package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostServiceTest {

    @Autowired
    private TagListRepository tagListRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    HttpServletRequest request;
    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request.getHeader("oauth")).thenReturn("kakao");
    }
    @Test
    void getBestRecipe() {
    }

    @Test
    void getRecentRecipeInfinite() {
    }

    @Test
    void getRecentRecipeTop10() {
    }

    @Test
    void getBestTagList() {
    }

    @Test
    void getResponsePostUserLike() {
    }

    @Test
    void getIngredientByPost() {
    }

    @Test
    void filterPostTitle() {
    }

    @Test
    void filterPostNickname() {
    }

    @Test
    void getAllTag() {
    }

    @Test
    void filterPostTag() {
    }

    @Test
    void isPostInTag() {
    }

    @Test
    void postLike() {
    }

    @Test
    void getUserPost() {
    }

    @Test
    void getUserLikePosts() {
    }

    @Test
    void getOtherUserPosts() {
    }

    @Test
    void getFollowRecipe() {
    }
}