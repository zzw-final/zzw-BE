package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeDetailRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
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

import static com.zzw.zzw_final.Dto.ErrorCode.NULL_FILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostDetailServiceTest {

    @MockBean
    private TokenProvider tokenProvider;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TagListRepository tagListRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private CommentRepository commentRepository;

    HttpServletRequest request;
    PostRecipeRequestDto requestDto;
    List<String> ingredient = new ArrayList<>();
    List<PostRecipeDetailRequestDto> postRecipeDetailRequestDtos = new ArrayList<>();

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnb29kOTcxMkBuYXRlLmNvbSIsImF1dGgiOiJST0xFX01FTUJFUiIsImV4cCI6MTY2NTU0NzE2N30.PQvOV9mzyNbtFPpY71XYlMjcjqpgN3HG2nzEChjMuo4");
        when(request.getHeader("Refresh-Token")).thenReturn("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NjYwNjU1Njd9.vJtmCwajGZzdsq4W8JsPbL1dymVy7CkYpkA0dl296_g");
        when(request.getHeader("oauth")).thenReturn("kakao");

        postRecipeDetailRequestDtos.add(new PostRecipeDetailRequestDto());
        ingredient.add("감자");
        ingredient.add("당근");
        requestDto= new PostRecipeRequestDto(ingredient, "", postRecipeDetailRequestDtos );

    }

    @Test
    void postRecipe() {

        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);


        Post post = new Post(requestDto, member);
        postRepository.save(post);

        Tag tag = tagRepository.findTagByName(requestDto.getFoodName());

        Tag tag1 = new Tag(requestDto.getFoodName());
        tagRepository.save(tag1);
        TagList tagList = new TagList(requestDto.getFoodName(), post, tag1, true);
        tagListRepository.save(tagList);


        List<String> ingredients = requestDto.getIngredient();

        for (String ingredientName : ingredients) {

            Tag tag2 = new Tag(ingredientName);
            tagRepository.save(tag2);
            TagList tagLists = new TagList(ingredientName, post, tag2, false);
            tagListRepository.save(tagLists);

            Assertions.assertEquals(tagLists.getName(), ingredientName);
            Assertions.assertEquals(tagLists.getTag(), tag2);
            Assertions.assertEquals(tagLists.getIsTitle(), false);
        }

        for (PostRecipeDetailRequestDto postRecipeDetailRequestDto : requestDto.getPageList()) {
            Content content = new Content(postRecipeDetailRequestDto, post);
            contentRepository.save(content);

            Assertions.assertEquals(content.getPost().getId(), post.getId());
        }

        //then
        Assertions.assertEquals(post.getMember(), member);
        Assertions.assertEquals(requestDto.getIngredient(), ingredient);
        Assertions.assertEquals(requestDto.getIngredient().size(), 2);
        Assertions.assertEquals(ingredients.size(), requestDto.getIngredient().size());
        Assertions.assertEquals(tag1.getName(), requestDto.getFoodName());
        Assertions.assertEquals(tagList.getName(), requestDto.getFoodName());
        Assertions.assertEquals(tagList.getTag(), tag1);
        Assertions.assertEquals(tagList.getIsTitle(), true);
        Assertions.assertEquals(requestDto.getTitle(),"제목");
        Assertions.assertEquals(requestDto.getTime(), "15분");
        Assertions.assertEquals(requestDto.getImageUrl(), "");
        Assertions.assertEquals(requestDto.getFoodName(), "닭볶음탕");
        Assertions.assertEquals(requestDto.getPageList(), postRecipeDetailRequestDtos);
        Assertions.assertEquals(requestDto.getPageList().size(), 1);
        Assertions.assertEquals(post.getTitle(), requestDto.getTitle());
        Assertions.assertEquals(post.getTime(), requestDto.getTime());
        Assertions.assertEquals(post.getLikeNum(), 0);
        Assertions.assertEquals(post.getThumbnail(), "https://zzwimage.s3.ap-northeast-2.amazonaws.com/Frame+3.png");
        Assertions.assertEquals(post.getMember(), member);
        Assertions.assertEquals(post.getUseremail(), member.getEmail());

    }

    @Test
    void putRecipe() {

        //when
        String token = request.getHeader("Authorization");
        String oauth = request.getHeader("oauth");
        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
        String email = tokenProvider.getUserEmail(token.substring(7));
        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);

        Post post = postRepository.findPostById(2661L);
        post.update(requestDto);
        List <Content> contents = contentRepository.findAllByPostOrderByPage(post);
        for(Content content : contents){
            contentRepository.delete(content);
        }

        for(PostRecipeDetailRequestDto postRecipeDetailRequestDto : requestDto.getPageList()){
            Content content2 = new Content(postRecipeDetailRequestDto, post);
            contentRepository.save(content2);
        }

        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        for (TagList tagList1: tagLists){
            tagListRepository.delete(tagList1);
        }

//        saveRecipeTag(requestDto.getFoodName(), post, true);

        List<String> ingredients = requestDto.getIngredient();

        for(String ingredient : ingredients){
//            saveRecipeTag(ingredient, post, false);
        }


        //then
        Assertions.assertEquals(post.getId(), 2661L);
    }



//
//    @Test
//    void deleteRecipe() {
//
//        //when
//        String token = request.getHeader("Authorization");
//        String oauth = request.getHeader("oauth");
//        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
//
//        Post post = postRepository.findPostById(post_id);
//
//        if (!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);
//
//        List<TagList> tagLists = tagListRepository.findAllByPost(post);
//        for (TagList tagList : tagLists){
//
//            Tag tag = tagRepository.findTagByName(tagList.getName());
//
//            if (tag.getCount() == 1){
//                tagRepository.delete(tag);
//            }else{
//                tag.countUpdate(tag.getCount()-1);
//            }
//            tagListRepository.delete(tagList);
//        }
//        postRepository.deleteById(post_id);
//
//        //then
//
//
//    }
//
//    @Test
//    void getRecipe() {
//
//        //when
//        String token = request.getHeader("Authorization");
//        String oauth = request.getHeader("oauth");
//        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
//
//        Member member = memberService.getMember(request);
//
//        Post post = postRepository.findPostById(post_id);
//
//        List<TagList> tagLists = tagListRepository.findAllByPost(post);
//        List<IngredientResponseDto> responseDtos = new ArrayList<>();
//
//        for (TagList tagList : tagLists)
//            responseDtos.add(new IngredientResponseDto(tagList));
//
//        List<ContentResponseDto> contentResponseDtos = new ArrayList<>();
//        List<Content> contentList = contentRepository.findAllByPostOrderByPage(post);
//        for (Content content : contentList)
//            contentResponseDtos.add(new ContentResponseDto(content));
//
//        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
//        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
//
//        for (Comment comment : commentList)
//            commentResponseDtos.add(new CommentResponseDto(comment));
//
//        //then
//
//
//    }
//
//    @Test
//    void postImage() {
//
//        //when
//        String token = request.getHeader("Authorization");
//        String oauth = request.getHeader("oauth");
//        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
//
//        if (multipartFile == null) {
//            return ResponseDto.fail(NULL_FILE);
//        }
//        String url = fileUploaderService.uploadImage(multipartFile);
//        return ResponseDto.success(new ImageUrlResponseDto(url));
//
//        //then
//    }
//
//    @Test
//    void getRecipeComment() {
//
//        //when
//        String token = request.getHeader("Authorization");
//        String oauth = request.getHeader("oauth");
//        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
//
//        Post post = postRepository.findPostById(post_id);
//
//        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
//
//        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
//
//        for (Comment comment : commentList)
//            commentResponseDtos.add(new CommentResponseDto(comment));
//
//        //then
//
//
//    }
//
//    @Test
//    void getRecipeByPage() {
//
//        //when
//        String token = request.getHeader("Authorization");
//        String oauth = request.getHeader("oauth");
//        when(tokenProvider.getUserEmail(token.substring(7))).thenReturn("good9712@nate.com");
//        String email = tokenProvider.getUserEmail(token.substring(7));
//        Member member = memberRepository.findMemberByEmailAndOauth(email, oauth);
//
//        Post post = postRepository.findPostById(post_id);
//
//        Content content = contentRepository.findContentByPostAndPage(post, page);
//
//        ContentResponseDto contentResponseDto = new ContentResponseDto(content);
//
//        //then
//
//    }
}