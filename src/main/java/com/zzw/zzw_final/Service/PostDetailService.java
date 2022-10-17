package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Request.PostRecipeDetailRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.zzw.zzw_final.Dto.ErrorCode.NULL_FILE;

@Service
@RequiredArgsConstructor
public class PostDetailService {
    private final MemberService memberService;
    private final FileUploaderService fileUploaderService;
    private final TagListRepository tagListRepository;
    private final TagRepository tagRepository;
    private final ContentRepository contentRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;


    public ResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = new Post(requestDto, member);
        postRepository.save(post);

        saveRecipeTag(requestDto.getFoodName(), post, true);

        List<String> ingredients = requestDto.getIngredient();

        for(String ingredientName : ingredients){
           saveRecipeTag(ingredientName, post, false);
        }

        for(PostRecipeDetailRequestDto postRecipeDetailRequestDto : requestDto.getPageList()){
            Content content = new Content(postRecipeDetailRequestDto, post);
            contentRepository.save(content);
        }

        if (memberService.isGetGrade(member, post))
            return ResponseDto.success(new PostRecipeResponseDto(post.getId(), true));
        else
            return ResponseDto.success(new PostRecipeResponseDto(post.getId(), false));
    }

    private void saveRecipeTag(String foodName, Post post, Boolean isTitle) {

        Tag tag = tagRepository.findTagByName(foodName);

        if (tag == null){
            Tag tag1 = new Tag(foodName);
            tagRepository.save(tag1);
            TagList tagList  = new TagList(foodName, post, tag1, isTitle);
            tagListRepository.save(tagList);
        }
        else{
            TagList tagList  = new TagList(foodName, post, tag, isTitle);
            tagListRepository.save(tagList);

            List<TagList> tagLists = tagListRepository.findAllByName(foodName);
            tag.setCount(tagLists.size());
        }
    }


    public ResponseDto<?> putRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    Long post_id) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        if(!member.getEmail().equals(post.getUseremail()))
            return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

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

        saveRecipeTag(requestDto.getFoodName(), post, true);

        List<String> ingredients = requestDto.getIngredient();

        for(String ingredient : ingredients){
            saveRecipeTag(ingredient, post, false);
        }

        return ResponseDto.success("success update");
    }


    public ResponseDto<?> deleteRecipe(HttpServletRequest request, Long post_id) {

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        if (!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        for (TagList tagList : tagLists){

            Tag tag = tagRepository.findTagByName(tagList.getName());

            if (tag.getCount() == 1){
                tagRepository.delete(tag);
            }else{
                tag.countUpdate(tag.getCount()-1);
            }
            tagListRepository.delete(tagList);
        }
        postRepository.deleteById(post_id);
        return ResponseDto.success("success delete");
    }

    public ResponseDto<?> getRecipe (Long post_id, HttpServletRequest request){

        Member member = memberService.getMember(request);

        Post post = postRepository.findPostById(post_id);

        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        List<IngredientResponseDto> responseDtos = new ArrayList<>();

        for (TagList tagList : tagLists)
            responseDtos.add(new IngredientResponseDto(tagList));

        List<ContentResponseDto> contentResponseDtos = new ArrayList<>();
        List<Content> contentList = contentRepository.findAllByPostOrderByPage(post);
        for (Content content : contentList)
            contentResponseDtos.add(new ContentResponseDto(content));

        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : commentList)
            commentResponseDtos.add(new CommentResponseDto(comment));

        return ResponseDto.success(getRecipeDetailResponseDto(member, post, responseDtos, contentResponseDtos));
    }

    private TimeResponseDto getRecipeDetailResponseDto(Member member, Post post, List<IngredientResponseDto> responseDtos, List<ContentResponseDto> contentResponseDtos) {
        if (member == null)
            return new TimeResponseDto(post, responseDtos, false, contentResponseDtos);
        else{
            PostLike postLikes = postLikeRepository.findPostLikesByPostAndMember(post, member);
            if (postLikes == null)
                return new TimeResponseDto(post, responseDtos, false, contentResponseDtos);
            else
                return new TimeResponseDto(post, responseDtos, true, contentResponseDtos);
        }
    }

    public ResponseDto<?> postImage (MultipartFile multipartFile){
        if (multipartFile == null) {
            return ResponseDto.fail(NULL_FILE);
        }
        String url = fileUploaderService.uploadImage(multipartFile);
        return ResponseDto.success(new ImageUrlResponseDto(url));
    }

    public ResponseDto<?> getRecipeComment (Long post_id){
        Post post = postRepository.findPostById(post_id);

        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);

        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : commentList)
            commentResponseDtos.add(new CommentResponseDto(comment));

        return ResponseDto.success(commentResponseDtos);
    }

    public ResponseDto<?> getRecipeByPage (Long post_id,int page){
        Post post = postRepository.findPostById(post_id);

        Content content = contentRepository.findContentByPostAndPage(post, page);

        ContentResponseDto contentResponseDto = new ContentResponseDto(content);

        return ResponseDto.success(contentResponseDto);
    }
}
