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

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();


        Post post = new Post(requestDto, member);
        postRepository.save(post);

        //음식 이름 -> 프론트로부터 받은 태그를 Tag, TagList에 저장 및 업데이트
        String foodname = requestDto.getFoodName();
        Tag tag = tagRepository.findTagByName(foodname);   // 이전에 음식 이름이 태그가 된 적이 있는가

        //이전에 없었던 새로운 태그라면
        if (tag == null){
            Tag tag1 = new Tag(foodname);
            tagRepository.save(tag1);
            TagList tagList  = new TagList(foodname, post, tag1, true);
            tagListRepository.save(tagList);
        }
        //아전에 있었던 태그라면
        else{
            TagList tagList  = new TagList(foodname, post, tag, true);
            tagListRepository.save(tagList);

            List<TagList> tagLists = tagListRepository.findAllByName(foodname);
            tag.setCount(tagLists.size());
        }

        //음식 재료 -> 프론트로부터 받은 태그를 Tag, TagList에 저장 및 업데이트
        List<String> ingredients = requestDto.getIngredient();

        for(String ingredientName : ingredients){
            String ingredient = ingredientName;

            Tag tag2 = tagRepository.findTagByName(ingredient);

            //이전에 없었던 새로운 태그라면
            if (tag2 == null){
                Tag tag1 = new Tag(ingredient);
                tagRepository.save(tag1);
                TagList tagList  = new TagList(ingredient, post, tag1, false);
                tagListRepository.save(tagList);
            }
            //이전에 있었던 태그라면
            else{
                TagList tagList  = new TagList(ingredient, post, tag2, false);
                tagListRepository.save(tagList);

                List<TagList> tagLists = tagListRepository.findAllByName(ingredient);
                tag2.setCount(tagLists.size());
            }
        }

        //게시글 내용과 이미지를 Content 데이터베이스에 담기
        for(PostRecipeDetailRequestDto postRecipeDetailRequestDto : requestDto.getPageList()){
            Content content = new Content(postRecipeDetailRequestDto, post);
            contentRepository.save(content);
        }

        return ResponseDto.success("success post");
    }


    //게시글 수정
    public ResponseDto<?> putRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    Long post_id) {

        //1. HttpServletRequest로 헤더에 있는 유저 확인
        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환

        Post post = postRepository.findPostById(post_id);
        if(!member.getEmail().equals(post.getUseremail()))
            return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //3. multipartFile가 null인지 아닌지 확인 후 -> null이면 url 변경 X, null이 아니면 update (Content -> image)

        //제목, 시간, 썸네일 페이지 수정
        post.update(requestDto);


        //상세 디테일 페이지 수정
        //디테일 페이지 찾아서 삭제 -> 다시 값 받아오기
        List <Content> content1 = contentRepository.findAllByPostOrderByPage(post);
        for(Content contents : content1){
            contentRepository.delete(contents);
        }

        //게시글 내용과 이미지를 Content 데이터베이스에 담기
        for(PostRecipeDetailRequestDto postRecipeDetailRequestDto : requestDto.getPageList()){
            Content content2 = new Content(postRecipeDetailRequestDto, post);
            contentRepository.save(content2);
        }

        //태그 수정
        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        for (TagList tagList1: tagLists){
            tagListRepository.delete(tagList1);
        }

        String foodname = requestDto.getFoodName();
        Tag tag = tagRepository.findTagByName(foodname);   // 이전에 음식 이름이 태그가 된 적이 있는가

        //이전에 없었던 새로운 태그라면
        if (tag == null){
            Tag tag1 = new Tag(foodname);
            tagRepository.save(tag1);
            TagList newTagList  = new TagList(foodname, post, tag1, true);
            tagListRepository.save(newTagList);
        }
        //이전에 있었던 태그라면
        else{
            TagList newTagList  = new TagList(foodname, post, tag, true);
            tagListRepository.save(newTagList);

            tag.setCount(tagListRepository.countAllByName(foodname).intValue());
        }

        //음식 재료 -> 프론트로부터 받은 태그를 Tag, TagList에 저장 및 업데이트
        List<String> ingredients = requestDto.getIngredient();

        for(String responseDto : ingredients){
            String ingredient = responseDto;

            Tag tag2 = tagRepository.findTagByName(ingredient);

            //이전에 없었던 새로운 태그라면
            if (tag2 == null){
                Tag tag1 = new Tag(ingredient);
                tagRepository.save(tag1);
                TagList tagList  = new TagList(ingredient, post, tag1, false);
                tagListRepository.save(tagList);
            }
            //이전에 있었던 태그라면
            else{
                TagList tagList  = new TagList(ingredient, post, tag2, false);
                tagListRepository.save(tagList);

                tag2.setCount(tagListRepository.countAllByName(ingredient).intValue());
            }

        }

        return ResponseDto.success("success update");
    }


    public ResponseDto<?> deleteRecipe(HttpServletRequest request, Long post_id) {

        //1. HttpServletRequest로 헤더에 있는 유저 확인

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환
        Post post = postRepository.findPostById(post_id);

        if (!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

            //3. 만약 작성인이 맞으면 delete !
        else {
            List<TagList> tagLists = tagListRepository.findAllByPost(post);
            for (TagList tagList : tagLists){
                String name = tagList.getName();
                Tag tag = tagRepository.findTagByName(name);
                if (tag.getCount() == 1){
                    tagRepository.delete(tag);
                }else{
                    tag.countUpdate(tag.getCount()-1);
                }

                tagListRepository.delete(tagList);
            }

            postRepository.deleteById(post_id);

        }

        return ResponseDto.success("success delete");
    }

    public ResponseDto<?> getRecipe (Long post_id, HttpServletRequest request){

        Member member = memberService.getMember(request);

        // post_id로 이에 맞는 Post 가져오기
        Post post = postRepository.findPostById(post_id);


        //Dto 생성자를 만들어서 1번에서 가져온 Post 정보 넣어주기
        List<TagList> tagLists = tagListRepository.findAllByPost(post);   //API 명세서에 있는 Response 대로 Dto 생성하기
        List<IngredientResponseDto> responseDtos = new ArrayList<>();

        for (TagList tagList : tagLists) {
            responseDtos.add(new IngredientResponseDto(tagList));
        }

        //레시피 별 페이지 내용 가져오기
        List<ContentResponseDto> contentResponseDtos = new ArrayList<>();
        List<Content> contentList = contentRepository.findAllByPostOrderByPage(post);
        for (Content content : contentList) {
            contentResponseDtos.add(new ContentResponseDto(content));
        }

        //댓글 가져오기
        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : commentList)
            commentResponseDtos.add(new CommentResponseDto(comment));


        if (member == null) {
            TimeResponseDto timeResponseDto = new TimeResponseDto(post, responseDtos, false, contentResponseDtos);
            return ResponseDto.success(timeResponseDto);
        } else {
            PostLike postLikes = postLikeRepository.findPostLikesByPostAndMember(post, member);
            if (postLikes == null) {
                TimeResponseDto timeResponseDto = new TimeResponseDto(post, responseDtos, false, contentResponseDtos);
                return ResponseDto.success(timeResponseDto);
            } else {
                TimeResponseDto timeResponseDto = new TimeResponseDto(post, responseDtos, true, contentResponseDtos);
                return ResponseDto.success(timeResponseDto);
            }
        }
    }

    public ResponseDto<?> postImage (MultipartFile multipartFile){
        //파일 -> 이미지 Url로 변경
        if (multipartFile == null) {
            //System.out.println(multipartFile);
            return ResponseDto.success("파일 값이 null임");
        }
        String url = fileUploaderService.uploadImage(multipartFile);
        return ResponseDto.success(new ImageUrlResponseDto(url));
    }

    public ResponseDto<?> getRecipeComment (Long post_id){
        Post post = postRepository.findPostById(post_id);

        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);

        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : commentList) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            commentResponseDtos.add(commentResponseDto);
        }

        return ResponseDto.success(commentResponseDtos);
    }

    public ResponseDto<?> getRecipeByPage (Long post_id,int page){
        Post post = postRepository.findPostById(post_id);

        Content content = contentRepository.findContentByPostAndPage(post, page);

        ContentResponseDto contentResponseDto = new ContentResponseDto(content);

        return ResponseDto.success(contentResponseDto);
    }

}
