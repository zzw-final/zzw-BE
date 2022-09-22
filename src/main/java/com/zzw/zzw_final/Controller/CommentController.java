package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/auth/post/{post_id}/comment")
    public ResponseDto<?> postComment(@RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request, @PathVariable Long post_id) {
        return commentService.postComment(requestDto, request, post_id);
    }

    @PutMapping("/api/auth/post/comment/{comment_id}")
    public ResponseDto<?> updateComment(@RequestBody CommentRequestDto requestDto,
                                        HttpServletRequest request, @PathVariable Long comment_id) {
        return commentService.updateComment(requestDto, request, comment_id);
    }

    @DeleteMapping("/api/auth/post/comment/{comment_id}")
    public ResponseDto<?> deleteComment(HttpServletRequest request, @PathVariable Long comment_id){
        return commentService.deleteComment(request, comment_id);
    }



}
