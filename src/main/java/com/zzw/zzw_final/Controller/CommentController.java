package com.zzw.zzw_final.Controller;

import com.zzw.zzw_final.Dto.Request.CommentRequestDto;
import com.zzw.zzw_final.Dto.Response.ResponseDto;
import com.zzw.zzw_final.Service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}
