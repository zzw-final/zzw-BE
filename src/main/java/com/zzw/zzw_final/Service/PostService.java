package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.IngredientRequestDto;
import com.zzw.zzw_final.Dto.Request.PostRecipeRequestDto;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.ContentRepository;
import com.zzw.zzw_final.Repository.PostRepository;
import com.zzw.zzw_final.Repository.TagListRepository;
import com.zzw.zzw_final.Repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final MemberService memberService;
    private final FileUploaderService fileUploaderService;
    private final TagListRepository tagListRepository;
    private final TagRepository tagRepository;
    private final ContentRepository contentRepository;
    private final PostRepository postRepository;

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
        List<IngredientRequestDto> ingredients = requestDto.getIngredient();

        for(IngredientRequestDto responseDto : ingredients){
            String ingredient = responseDto.getIngredientName();

            Tag tag2 = tagRepository.findTagByName(ingredient);

            //이전에 없었던 새로운 태그라면
            if (tag2 == null){
                Tag tag1 = new Tag(ingredient);
                tagRepository.save(tag1);
                TagList tagList  = new TagList(ingredient, post, tag1, false);
                tagListRepository.save(tagList);
            }
            //아전에 있었던 태그라면
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

    public ResponseDto<?> putRecipe(PostRecipeRequestDto requestDto, HttpServletRequest request,
                                    MultipartFile multipartFile, Long post_id) {
        //1. HttpServletRequest로 헤더에 있는 유저 확인

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환

        //3. multipartFile가 null인지 아닌지 확인 후 -> null이면 url 변경 X, null이 아니면 update (Content -> image)

        //4. PostRecipeRequestDto에 있는 값들 중에 ""이 아닌 것만 update
        // (Content -> 글 내용) (Post -> 글 제목, 조리시간) (TagList -> 재료, 요리제목)


        return ResponseDto.success("success update");
    }


    public ResponseDto<?> deleteRecipe(HttpServletRequest request, Long post_id) {

        //1. HttpServletRequest로 헤더에 있는 유저 확인

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환

        //3. 만약 작성인이 맞으면 delete !


        return ResponseDto.success("success delete");
    }

    public ResponseDto<?> getBestRecipe() {

        //제일 등록된 태그 상위 5개 리스트에 담기
        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++)
            tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

        //제일 좋아요 많은 순으로 레시피 리스트에 담기
        List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
        List<PostResponseDto> best_postResponseDtos = new ArrayList<>();

        for(Post post : best_posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            best_postResponseDtos.add(new PostResponseDto(post, content, ingredientResponseDtos));
        }

        //제일 최신 순으로 레시피 리스트에 담기
        List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

        for(Post post : recent_posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            recent_postResponseDtos.add(new PostResponseDto(post, content, ingredientResponseDtos));
        }

        MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(tagResponseDtos, best_postResponseDtos, recent_postResponseDtos);
        return ResponseDto.success(mainPostResponseDto);
    }

    public List<IngredientResponseDto> getIngredientByPost(Post post) {
        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
        for (TagList tagList : tagLists)
            ingredientResponseDtos.add(new IngredientResponseDto(tagList));

        return ingredientResponseDtos;
    }
}
