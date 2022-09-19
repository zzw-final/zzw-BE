package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Request.IngredientResponseDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.ContentRepository;
import com.zzw.zzw_final.Repository.PostRepository;
import com.zzw.zzw_final.Repository.TagListRepository;
import com.zzw.zzw_final.Repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberService memberService;
    private final FileUploaderService fileUploaderService;
    private final TagListRepository tagListRepository;
    private final TagRepository tagRepository;
    private final ContentRepository contentRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;


    @Transactional
    public ResponseDto<?> postRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request, MultipartFile multipartFile) {

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //파일 -> 이미지 Url로 변경
        String url = fileUploaderService.uploadImage(multipartFile);

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
        List<IngredientResponseDto> ingredients = requestDto.getIngredient();

        for(IngredientResponseDto responseDto : ingredients){
            String ingredient = responseDto.getIngredientName();

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
        Content content = new Content(url, requestDto.getContent(), post);
        contentRepository.save(content);

        return ResponseDto.success("success post");
    }


    //게시글 수정
    public ResponseDto<?> putRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    MultipartFile multipartFile, Long post_id) {
        //1. HttpServletRequest로 헤더에 있는 유저 확인

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환

        Post post = postRepository.findPostById(post_id);
        if(!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //3. multipartFile가 null인지 아닌지 확인 후 -> null이면 url 변경 X, null이 아니면 update (Content -> image)

        if(multipartFile != null){
            //제목, 시간 수정
            String url = fileUploaderService.uploadImage(multipartFile);
            post.update(requestDto);

            //내용, 사진 수정
            Content content = contentRepository.findContentByPost(post);
            content.update(requestDto, url);

            //태그 수정
            List<TagList> tagList = tagListRepository.findAllByPost(post);
            for (TagList tagList1: tagList){
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
            //아전에 있었던 태그라면
            else{
                TagList newTagList  = new TagList(foodname, post, tag, true);
                tagListRepository.save(newTagList);

                List<TagList> tagLists = tagListRepository.findAllByName(foodname);
                tag.setCount(tagLists.size());
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

        if(!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //3. 만약 작성인이 맞으면 delete !
        else {
            postRepository.deleteById(post_id);
        }

        return ResponseDto.success("success delete");


    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

}

