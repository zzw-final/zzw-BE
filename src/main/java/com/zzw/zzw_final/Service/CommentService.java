package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.Comment;
import com.zzw.zzw_final.Dto.Entity.Member;
import com.zzw.zzw_final.Dto.Entity.Post;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Response.CommentResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.CommentRepository;
import com.zzw.zzw_final.Repository.MemberRepository;
import com.zzw.zzw_final.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public ResponseDto<?> postComment(CommentRequestDto requestDto, HttpServletRequest request, Long post_id){

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Post post = postRepository.findPostById(post_id);

        Comment comment = new Comment(requestDto, post, member);
        commentRepository.save(comment);

        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        return ResponseDto.success(commentResponseDto);
    }

    @Transactional
    public ResponseDto<?> updateComment(CommentRequestDto requestDto, HttpServletRequest request, Long comment_id){

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Comment comment = commentRepository.findCommentById(comment_id);

        if(!member.getEmail().equals(comment.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        comment.update(requestDto);

        return ResponseDto.success("success comment update");
    }

    @Transactional
    public ResponseDto<?> deleteComment(HttpServletRequest request, Long comment_id){

        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        Comment comment = commentRepository.findCommentById(comment_id);
        if(!member.getEmail().equals(comment.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        else {
            commentRepository.deleteById(comment_id);
        }
        return ResponseDto.success("success delete");
    }

}

