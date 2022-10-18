package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import com.zzw.zzw_final.Dto.Entity.Tag;
import com.zzw.zzw_final.Dto.Entity.TagList;
import com.zzw.zzw_final.Dto.Response.*;
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
        //when
        Long lastPostId = 4378L;
        List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> postResponseDtos = new ArrayList<>();
        Post post = postRepository.findPostById(lastPostId);
        int index = (recent_posts.indexOf(post) == 0) ? 0 : recent_posts.indexOf(post) + 1;

        int size = recent_posts.size();
        int endIndex = index + 6 > size ? size : index + 6;

        for (int i = index; i < endIndex; i++) {
            List<TagList> tagLists = tagListRepository.findAllByPost(recent_posts.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), recent_posts.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            postResponseDtos.add(new PostResponseDto(recent_posts.get(i), ingredientResponseDtos));
            Assertions.assertEquals(tagLists.size(), postResponseDtos.get(i).getIngredient().size());
        }

        //then
        Assertions.assertEquals(post.getId(), 4378L);
        Assertions.assertEquals(postResponseDtos.size(), 6);
        Assertions.assertEquals(index, 0);
        Assertions.assertEquals(endIndex, 6);
        Assertions.assertEquals(size, recent_posts.size());
        Assertions.assertEquals(endIndex-index, 6);
    }

    @Test
    void getRecentRecipeTop10() {
        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> postResponseDtos = new ArrayList<>();

        int postSize = (recent_posts.size() < 10) ? recent_posts.size() : 10;

        for (int i = 0; i < postSize; i++) {
            List<TagList> tagLists = tagListRepository.findAllByPost(recent_posts.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), recent_posts.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            postResponseDtos.add(new PostResponseDto(recent_posts.get(i), ingredientResponseDtos));
            Assertions.assertEquals(tagLists.size(), postResponseDtos.get(i).getIngredient().size());
        }

        //then
        Assertions.assertEquals(token, "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        Assertions.assertEquals(oauth, "kakao");
        Assertions.assertEquals(email, "good9712@nate.com");
        Assertions.assertEquals(member.getEmail(), "good9712@nate.com");
        Assertions.assertEquals(member.getOauth(), "kakao");
        Assertions.assertEquals(recent_posts.size(), 13);
        Assertions.assertEquals(postSize, 10);
        Assertions.assertEquals(postResponseDtos.size(), 10);
    }

    @Test
    void getBestTagList() {
        //when
        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

        //then
        Assertions.assertEquals(tags.size(), 55);
        Assertions.assertEquals(tagResponseDtos.size(), 5);
    }

    @Test
    void getIngredientByPost() {
        //when
        Post post = postRepository.findPostById(2559L);

        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
        for (TagList tagList : tagLists)
            ingredientResponseDtos.add(new IngredientResponseDto(tagList));

        //then
        Assertions.assertEquals(post.getId(), 2559L);
        Assertions.assertEquals(tagLists.size(), 4);
    }

    @Test
    void filterPostTitle() {
        //when
        String title = "김";
        List<Post> posts = postRepository.findAllByTitleContaining(title);
        List<PostResponseDto> Posts = new ArrayList<>();

        Long lastPostId = posts.get(0).getId();

        Post post = postRepository.findPostById(lastPostId);
        int index = (posts.indexOf(post) == 0) ? 0 : posts.indexOf(post) + 1;

        int size = posts.size();
        int endIndex = index + 8 > size ? size : index + 8;

        for (int i = index; i < endIndex; i++) {
            List<TagList> tagLists = tagListRepository.findAllByPost(posts.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), posts.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            Posts.add(new PostResponseDto(posts.get(i), ingredientResponseDtos, false));
        }

        //then
        Assertions.assertEquals(posts.size(), 2);
        Assertions.assertEquals(lastPostId, 2559L);
        Assertions.assertEquals(post.getId(), lastPostId);
        Assertions.assertEquals(index, 0);
        Assertions.assertEquals(size,2);
        Assertions.assertEquals(endIndex, 2);
        Assertions.assertEquals(Posts.size(), endIndex-index);
        Assertions.assertEquals(endIndex-index, 2);
    }

    @Test
    void filterPostNickname() {
        //when
        String nickname = "공듀";

        List<Member> members = memberRepository.findAllByNicknameContaining(nickname);
        List<PostResponseDto> responseDtos = new ArrayList<>();
        List<Post> containPost = new ArrayList<>();

        for (Member member : members) {
            List<Post> posts = postRepository.findAllByMember(member);
            for(Post post : posts)
                containPost.add(post);
        }

        Long lastPostId = containPost.get(0).getId();

        Post post = postRepository.findPostById(lastPostId);
        int index = (containPost.indexOf(post) == 0) ? 0 : containPost.indexOf(post) + 1;

        int size = containPost.size();
        int endIndex = index + 8 > size ? size : index + 8;

        for (int i = index; i < endIndex; i++) {
            List<TagList> tagLists = tagListRepository.findAllByPost(containPost.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), containPost.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            responseDtos.add(new PostResponseDto(containPost.get(i), ingredientResponseDtos));
        }

        //then
        Assertions.assertEquals(members.size(), 2);
        Assertions.assertEquals(responseDtos.size(), 4);
        Assertions.assertEquals(lastPostId, 2559L);
        Assertions.assertEquals(post.getId(), 2559L);
        Assertions.assertEquals(index, 0);
        Assertions.assertEquals(size, 4);
        Assertions.assertEquals(endIndex, 4);
    }

    @Test
    void getAllTag() {
        //when
        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

        int tagSize = (tags.size() < 100) ? tags.size() : 100;

        for (int i = 0; i < tagSize; i++) {
            tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));
        }

        //then
        Assertions.assertEquals(tags.size(), 55);
        Assertions.assertEquals(tagSize, 55);
        Assertions.assertEquals(tagResponseDtos.size(), 55);
    }

    @Test
    void filterPostTag() {
        //when
        String tag = "김치,된장찌개";
        List<Post> response_posts = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        String[] tag_list = tag.split(",");

        for (Post post : posts){
            int count = 0;
            for (int i = 0; i < tag_list.length; i++) {
                String post_tag = tag_list[i];

                for (TagList postTag : post.getTagLists()) {
                    if (postTag.getName().equals(post_tag)) {
                        count++;
                    }
                }
            }
            if (count >= tag_list.length)
                response_posts.add(post);
        }

        List<PostResponseDto> responseDtos = new ArrayList<>();
        Long lastPostId = response_posts.get(0).getId();

        Post post = postRepository.findPostById(lastPostId);
        int index = (response_posts.indexOf(post) == 0) ? 0 : response_posts.indexOf(post) + 1;

        int size = response_posts.size();
        int endIndex = index + 8 > size ? size : index + 8;

        for (int i = index; i < endIndex; i++) {
            List<TagList> tagLists = tagListRepository.findAllByPost(response_posts.get(i));
            List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
            for (TagList tagList : tagLists) {
                Assertions.assertEquals(tagList.getPost(), response_posts.get(i));
                ingredientResponseDtos.add(new IngredientResponseDto(tagList));
            }
            responseDtos.add(new PostResponseDto(response_posts.get(i), ingredientResponseDtos));
        }

        //then
        Assertions.assertEquals(posts.size(), 13);
        Assertions.assertEquals(tag_list.length, 2);
        Assertions.assertEquals(tag_list[0], "김치");
        Assertions.assertEquals(tag_list[1], "된장찌개");
        Assertions.assertEquals(response_posts.size(), 1);
        Assertions.assertEquals(lastPostId, 4378L);
        Assertions.assertEquals(post.getId(), 4378L);
        Assertions.assertEquals(size, 1);
        Assertions.assertEquals(endIndex, 1);
        Assertions.assertEquals(responseDtos.size(), 1);
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