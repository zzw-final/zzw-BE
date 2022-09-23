package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Config.Jwt.TokenProvider;
import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.*;

import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
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

    private final MemberRepository memberRepository;

    private final PostLikeRepository postLikeRepository;

    private final CommentRepository commentRepository;
    @Transactional
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
            //이전에 있었던 태그라면
            else{
                TagList tagList  = new TagList(ingredient, post, tag2, false);
                tagListRepository.save(tagList);

                List<TagList> tagLists = tagListRepository.findAllByName(ingredient);
                tag2.setCount(tagLists.size());
            }

        }

        //게시글 내용과 이미지를 Content 데이터베이스에 담기
        Content content = new Content(requestDto.getImageUrl(), requestDto.getContent(), post);
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
            //이전에 있었던 태그라면
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

    public ResponseDto<?> filterPostTitle(FilterPostByTitleRequestDto requestDto) {
        String title = requestDto.getTitle();

        List<Post> posts = postRepository.findAllByTitleContaining(title);
        List<PostResponseDto> Posts = new ArrayList<>();

        for(Post post : posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            Posts.add(new PostResponseDto(post, content, ingredientResponseDtos));
        }

        return ResponseDto.success(Posts);
    }

    public ResponseDto<?> filterPostNickname(FilterPostByNicknameRequestDto requestDto) {
        String nickname = requestDto.getNickname();

        List<Member> members = memberRepository.findAllByNicknameContaining(nickname);
        List<PostResponseDto> responseDtos = new ArrayList<>();

        for (Member member : members){
            List<Post> posts = postRepository.findAllByMember(member);
            for(Post post : posts){
                Content content = contentRepository.findContentByPost(post);
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
                responseDtos.add(new PostResponseDto(post, content, ingredientResponseDtos));
            }
        }
        return ResponseDto.success(responseDtos);
    }

    public ResponseDto<?> getRecipe(Long post_id) {

        // post_id로 이에 맞는 Post 가져오기
        Post post = postRepository.findPostById(post_id);

        Content content = contentRepository.findContentByPost(post);

        //Dto 생성자를 만들어서 1번에서 가져온 Post 정보 넣어주기
        List<TagList> tagLists = tagListRepository.findAllByPost(post);   //API 명세서에 있는 Response 대로 Dto 생성하기
        List<IngredientResponseDto> responseDtos = new ArrayList<>();

        for(TagList tagList : tagLists){
            responseDtos.add(new IngredientResponseDto(tagList));
        }

        List<Comment> commentList = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : commentList)
            commentResponseDtos.add(new CommentResponseDto(comment));

        TimeResponseDto timeResponseDto = new TimeResponseDto(post, content, responseDtos, commentResponseDtos);

        return ResponseDto.success(timeResponseDto);
    }

    public ResponseDto<?> getAllTag() {

        List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
        List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();
        if(tags.size() < 100){
            for(Tag tag : tags){
                tagResponseDtos.add(new BestTagResponseDto(tag));
            }
        }else{
            for(int i=0; i < 100; i++){
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));
            }
        }
        return ResponseDto.success(tagResponseDtos);
    }

    public ResponseDto<?> filterPostTag(FilterTagListRequestDto requestDto) {
        List<Post> response_posts = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        for(Post post : posts){
            if (isPostinTag(post, requestDto)){
                response_posts.add(post);
            }
        }

        List<PostResponseDto> responseDtos = new ArrayList<>();
        for(Post post : response_posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            responseDtos.add(new PostResponseDto(post, content, ingredientResponseDtos));
        }

        return ResponseDto.success(responseDtos);
    }
    public Boolean isPostinTag(Post post, FilterTagListRequestDto requestDto){
        int count = 0;

        for(int i=0; i<requestDto.getTagList().size(); i++){
            String tag = requestDto.getTagList().get(i).getTagName();

            for (TagList tagList : post.getTagLists()) {
                if (tagList.getName().equals(tag)) {
                    count ++;
                }
            }
        }
        if (count >= requestDto.getTagList().size())
            return true;
        else
            return false;
    }

    public ResponseDto<?> postLike(Long post_id, HttpServletRequest request) {
        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        //이전에 해당 게시글에 좋아요를 한 적이 있는지 판단
        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);

        // 이전에 이 게시물에 좋아요를 한 적이 없음 -> 좋아요 수락
        if(postLike == null){
            PostLike userLike = new PostLike(member, post);
            postLikeRepository.save(userLike);

            List<PostLike> postLikes = postLikeRepository.findPostLikesByPost(post);
            //post.setLikeNum(postLikes.size());  // -> likeNum 업데이트
            post.update(postLikes.size());
            //postRepository.save(post);

            return ResponseDto.success("post like success");
        }
        // 이전에 이 게시물에 좋아요를 한 적이 있음 -> 좋아요 취소
        else{
            postLikeRepository.delete(postLike);

            List<PostLike> postLikes = postLikeRepository.findPostLikesByPost(post);
            post.setLikeNum(postLikes.size()); // -> likeNum 업데이트
            postRepository.save(post);

            return ResponseDto.success("post like delete success");
        }
    }

    public ResponseDto<?> postImage(MultipartFile multipartFile) {
        //파일 -> 이미지 Url로 변경
        String url = fileUploaderService.uploadImage(multipartFile);
        return ResponseDto.success(new ImageUrlResponseDto(url));
    }

    /*

    private boolean verifiedMember(HttpServletRequest request, Member member) {
        String token = request.getHeader("Authorization").substring((7));
        if (token == null || !tokenProvider.validateToken(token)){
            return false;
        }
        Authentication authentication = tokenProvider.getAuthentication(token);
        String memberId = authentication.getName(); //member Id
        if (Long.valueOf(memberId)!= member.getId()){
            return false;
        }
        return true;
    }

    @Transactional
    public Member getMemberfromContext() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Member> member = memberRepository.findById(Long.valueOf(userId));  //Long.valueOf(userId)
        return member.get();
    }

     */
}

