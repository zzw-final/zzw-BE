package com.zzw.zzw_final.Service;

import com.zzw.zzw_final.Dto.Entity.*;
import com.zzw.zzw_final.Dto.ErrorCode;
import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Response.CommentGradeResponseDto;
import com.zzw.zzw_final.Dto.Response.CommentResponseDto;
import com.zzw.zzw_final.Dto.Response.GetGradeResponseDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
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
        //댓글 10개면 참견왕
        List<Comment> comments = commentRepository.findAllByMember(member);
        if(comments.size() >= 10){
            if(memberService.isMemberGetGrade(5008L, member)){
                return ResponseDto.success(new CommentGradeResponseDto(true, commentResponseDto));
            }
        }
        return ResponseDto.success(new CommentGradeResponseDto(false, commentResponseDto));

    }



    @Transactional
    public ResponseDto<?> updateComment(CommentRequestDto requestDto, HttpServletRequest request, Long comment_id){

        //로그인 토큰 유효성 검증하기
        ResponseDto<?> result = memberService.checkMember(request);
        Member member = (Member) result.getData();

        //수정할 댓글을 찾아 저장해준다(댓글번호)
        //댓글 작성자가 맞는지 확인
        Comment comment = commentRepository.findCommentById(comment_id);

        if(!member.getEmail().equals(comment.getUseremail())) return ResponseDto.fail(ErrorCode.NOT_EQUAL_MEMBER);

        //댓글 내용 수정
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

