package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.Request.*;

import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Response.*;
import com.zzw.zzw_final.Repository.*;
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
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
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
                                    Long post_id) {
        //1. HttpServletRequest로 헤더에 있는 유저 확인

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //2. post_id의 게시물이 유저가 글 작성인이 맞는지 확인 -> 아닐 시 오류 반환

        Post post = postRepository.findPostById(post_id);
        if(!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //3. multipartFile가 null인지 아닌지 확인 후 -> null이면 url 변경 X, null이 아니면 update (Content -> image)

        //제목, 시간 수정
        post.update(requestDto);

        //내용, 사진 수정
        Content content = contentRepository.findContentByPost(post);
        content.update(requestDto);

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

        if(!member.getEmail().equals(post.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //3. 만약 작성인이 맞으면 delete !
        else {
            postRepository.deleteById(post_id);
        }

        return ResponseDto.success("success delete");
    }

    public ResponseDto<?> getBestRecipe(HttpServletRequest request) {

        Member member = memberService.getMember(request);

        //베스트만 보여주기 (팔로우, 팔로워 X)
        if (member == null){
            //제일 등록된 태그 상위 5개 리스트에 담기
            List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
            List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

            for (int i = 0; i < 5; i++)
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

            //제일 좋아요 많은 순으로 레시피 리스트에 담기
            List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
            List<PostResponseDto> best_postResponseDtos = new ArrayList<>();
            if (best_posts.size() < 10) {
                for(int i=0; i < best_posts.size(); i++){
                    Content content = contentRepository.findContentByPost(best_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), content, ingredientResponseDtos));
                }
            }else{
                for(int i=0; i < 10; i++){
                    Content content = contentRepository.findContentByPost(best_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(new PostResponseDto(best_posts.get(i), content, ingredientResponseDtos));
                }
            }

            //제일 최신 순으로 레시피 리스트에 담기
            List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
            List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

            if (recent_posts.size() < 10){
                for(int i = 0; i < recent_posts.size(); i++){
                    Content content = contentRepository.findContentByPost(recent_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(new PostResponseDto(recent_posts.get(i), content, ingredientResponseDtos));
                }
            }else{
                for(int i = 0; i < 10; i++){
                    Content content = contentRepository.findContentByPost(recent_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(new PostResponseDto(recent_posts.get(i), content, ingredientResponseDtos));
                }
            }

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(tagResponseDtos, best_postResponseDtos, recent_postResponseDtos);
            return ResponseDto.success(mainPostResponseDto);
        }else{
            //제일 등록된 태그 상위 5개 리스트에 담기
            List<Tag> tags = tagRepository.findAllByOrderByCountDesc();
            List<BestTagResponseDto> tagResponseDtos = new ArrayList<>();

            for (int i = 0; i < 5; i++)
                tagResponseDtos.add(new BestTagResponseDto(tags.get(i)));

            //제일 좋아요 많은 순으로 레시피 리스트에 담기
            List<Post> best_posts = postRepository.findAllByOrderByLikeNumDesc();
            List<PostResponseDto> best_postResponseDtos = new ArrayList<>();

            if (best_posts.size() < 10) {
                for(int i=0; i < best_posts.size(); i++){
                    Content content = contentRepository.findContentByPost(best_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(getResponsePostUserLike(member, best_posts.get(i), content, ingredientResponseDtos));
                }
            }else{
                for(int i=0; i < 10; i++){
                    Content content = contentRepository.findContentByPost(best_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(best_posts.get(i));
                    best_postResponseDtos.add(getResponsePostUserLike(member, best_posts.get(i), content, ingredientResponseDtos));
                }
            }

            //제일 최신 순으로 레시피 리스트에 담기
            List<Post> recent_posts = postRepository.findAllByOrderByCreatedAtDesc();
            List<PostResponseDto> recent_postResponseDtos = new ArrayList<>();

            if (recent_posts.size() < 10){
                for(int i = 0; i < recent_posts.size(); i++){
                    Content content = contentRepository.findContentByPost(recent_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(getResponsePostUserLike(member, recent_posts.get(i), content, ingredientResponseDtos));
                }
            }else{
                for(int i = 0; i < 10; i++){
                    Content content = contentRepository.findContentByPost(recent_posts.get(i));
                    List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(recent_posts.get(i));
                    recent_postResponseDtos.add(getResponsePostUserLike(member, recent_posts.get(i), content, ingredientResponseDtos));
                }
            }

            //팔로우 레시피 리스트 팔로우 당 1개씩 보내기
            List<Follow> follows = followRepository.findAllByFollowerId(member.getId());
            List<Post> followPost = new ArrayList<>();

            for (Follow follow : follows){
                Long followId = follow.getMember().getId();
                Member followmember = memberRepository.findMemberById(follow.getMember().getId());
                List<Post> userPost = postRepository.findAllByMemberOrderByCreatedAtDesc(followmember);
                if (userPost.size() != 0){
                    followPost.add(userPost.get(0));
                }
            }

            List<PostResponseDto> follow_postResponseDtos = new ArrayList<>();

            for(Post post : followPost){
                Content content = contentRepository.findContentByPost(post);
                List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
                follow_postResponseDtos.add(getResponsePostUserLike(member, post, content, ingredientResponseDtos));
            }

            MainPostResponseDto mainPostResponseDto = new MainPostResponseDto(tagResponseDtos, best_postResponseDtos,
                    recent_postResponseDtos, follow_postResponseDtos);
            return ResponseDto.success(mainPostResponseDto);
        }


    }

    // 유저가 해당 게시물에 좋아요 했는지 여부를 판단해서 Dto로 반환하는 함수
    public PostResponseDto getResponsePostUserLike(Member member, Post post, Content content, List<IngredientResponseDto> ingredientResponseDtos) {
        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);
        if (postLike == null){
            return new PostResponseDto(post, content, ingredientResponseDtos);
        }else{
            return new PostResponseDto(post, content, ingredientResponseDtos, true);
        }
    }

    public List<IngredientResponseDto> getIngredientByPost(Post post) {
        List<TagList> tagLists = tagListRepository.findAllByPost(post);
        List<IngredientResponseDto> ingredientResponseDtos = new ArrayList<>();
        for (TagList tagList : tagLists)
            ingredientResponseDtos.add(new IngredientResponseDto(tagList));

        return ingredientResponseDtos;
    }

    public ResponseDto<?> filterPostTitle(String  title) {

        List<Post> posts = postRepository.findAllByTitleContaining(title);
        List<PostResponseDto> Posts = new ArrayList<>();

        for(Post post : posts){
            Content content = contentRepository.findContentByPost(post);
            List<IngredientResponseDto> ingredientResponseDtos = getIngredientByPost(post);
            Posts.add(new PostResponseDto(post, content, ingredientResponseDtos));
        }

        return ResponseDto.success(Posts);
    }

    public ResponseDto<?> filterPostNickname(String nickname) {

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

    public ResponseDto<?> getRecipe(Long post_id, HttpServletRequest request) {

        Member member = memberService.getMember(request);

        // post_id로 이에 맞는 Post 가져오기
        Post post = postRepository.findPostById(post_id);

        Content content = contentRepository.findContentByPost(post);

        //Dto 생성자를 만들어서 1번에서 가져온 Post 정보 넣어주기
        List<TagList> tagLists = tagListRepository.findAllByPost(post);   //API 명세서에 있는 Response 대로 Dto 생성하기
        List<IngredientResponseDto> responseDtos = new ArrayList<>();

        for(TagList tagList : tagLists){
            responseDtos.add(new IngredientResponseDto(tagList));
        }

        List<Comment> commentList = commentRepository.findAllByPostOrderByCreatedAtDesc(post);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : commentList)
            commentResponseDtos.add(new CommentResponseDto(comment));

        if (member == null){
            TimeResponseDto timeResponseDto = new TimeResponseDto(post, content, responseDtos, commentResponseDtos, false);
            return ResponseDto.success(timeResponseDto);
        }
        else {
            PostLike postLikes = postLikeRepository.findPostLikesByPostAndMember(post, member);
            if (postLikes == null){
                TimeResponseDto timeResponseDto = new TimeResponseDto(post, content, responseDtos, commentResponseDtos, false);
                return ResponseDto.success(timeResponseDto);
            }else{
                TimeResponseDto timeResponseDto = new TimeResponseDto(post, content, responseDtos, commentResponseDtos, true);
                return ResponseDto.success(timeResponseDto);
            }
        }
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

    public ResponseDto<?> filterPostTag(String tag) {
        List<Post> response_posts = new ArrayList<>();
        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();

        String[] tag_list = tag.split(",");

        for(Post post : posts){
            if (isPostinTag(post, tag_list)){
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
    public Boolean isPostinTag(Post post, String[] tagList){
        int count = 0;

        for(int i=0; i<tagList.length; i++){
            String tag = tagList[i];

            for (TagList postTag : post.getTagLists()) {
                if (postTag.getName().equals(tag)) {
                    count ++;
                }
            }
        }
        if (count >= tagList.length)
            return true;
        else
            return false;
    }

    public ResponseDto<?> postLike(Long post_id, HttpServletRequest request) {
        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        if (post == null){
            return ResponseDto.fail(ErrorCode.NOTFOUND_POST_ID);
        }

        //이전에 해당 게시글에 좋아요를 한 적이 있는지 판단
        PostLike postLike = postLikeRepository.findPostLikesByPostAndMember(post, member);

        // 이전에 이 게시물에 좋아요를 한 적이 없음 -> 좋아요 수락
        if(postLike == null){
            PostLike userLike = new PostLike(member, post);
            postLikeRepository.save(userLike);

            post.setLikeNum(postLikeRepository.countAllByPost(post).intValue());  // -> likeNum 업데이트
            postRepository.save(post);

            return ResponseDto.success("post like success");
        }
        // 이전에 이 게시물에 좋아요를 한 적이 있음 -> 좋아요 취소
        else{
            postLikeRepository.delete(postLike);

            post.setLikeNum(postLikeRepository.countAllByPost(post).intValue());  // -> likeNum 업데이트
            postRepository.save(post);

            return ResponseDto.success("post like delete success");
        }
    }

    public ResponseDto<?> postImage(MultipartFile multipartFile) {
        //파일 -> 이미지 Url로 변경
        if (multipartFile == null){
            //System.out.println(multipartFile);
            return ResponseDto.success("파일 값이 null임");
        }
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

