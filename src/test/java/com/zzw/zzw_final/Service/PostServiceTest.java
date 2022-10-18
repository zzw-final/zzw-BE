package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import com.zzw.zzw_final.Dto.Entity.TagList;
import com.zzw.zzw_final.Dto.Response.IngredientResponseDto;
import com.zzw.zzw_final.Dto.Response.PostResponseDto;
import com.zzw.zzw_final.Repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

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
    @MockBean
    private TokenProvider tokenProvider;

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
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
        List<PostResponseDto> best_postResponseDtos = new ArrayList<>();

        for(int i = 0; i<10; i++){
            List<TagList> tagLists = tagListRepository.findAllByPost(best_posts.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), best_posts.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), ingredientResponseDtos));
            Assertions.assertEquals(tagLists.size(), best_postResponseDtos.get(i).getIngredient().size());
        }

        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(best_postResponseDtos.size(), 10);
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