package com.zzw.zzw_final.Dto.Entity;

import com.sun.istack.NotNull;
import com.zzw.zzw_final.Dto.GoogleLoginDto;
import com.zzw.zzw_final.Dto.OauthUserDto;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter // get 함수를 일괄적으로 만들어줍니다.
@NoArgsConstructor // 기본 생성자를 만들어줍니다.
@Entity // DB 테이블 역할을 합니다.
public class Member {

    @Id // ID 값, Primary Key로 사용하겠다는 뜻입니다.
    @GeneratedValue(strategy = GenerationType.AUTO) // 자동 증가 명령입니다.
    private Long id;

    @Column
    @NonNull
    private String email;

    @Column
    @NonNull
    private String nickname;

    @Column
    @NotNull
    private String oauth;

    @Column
    private String profile;

    @Column
    private String grade;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> follows;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grade> grades;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRead> chatReads;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Profile> profiles;

    public Member(String email, String kakao) {
        this.email = email;
        this.nickname = kakao;
        this.oauth = "kakaoUser";
        this.grade = "준비중";
        this.profile = "https://postimagestorage.s3.amazonaws.com/mini_project/zzw.-removebg-preview.png";
    }

    public Member(GoogleLoginDto googleUser) {
        this.nickname = "google";
        this.email = googleUser.getEmail();
        this.oauth = "googleUser";
        this.grade = "준비중";
        this.profile = "https://postimagestorage.s3.amazonaws.com/mini_project/zzw.-removebg-preview.png";
    }

    public Member(SignupRequestDto requestDto){
        this.nickname = requestDto.getNickname();
        this.email = requestDto.getEmail();
        this.oauth = requestDto.getOauth();
        this.grade = "베타테스터";
        this.profile = "https://zzwimage.s3.ap-northeast-2.amazonaws.com/zzw.-removebg-preview.png";
    }

    public Member(OauthUserDto oauthUserDto){
        this.nickname = "naver";
        this.email = oauthUserDto.getEmail();
        this.grade = "준비중";
        this.profile = "https://postimagestorage.s3.amazonaws.com/mini_project/zzw.-removebg-preview.png";
        this.oauth = "naverUser";
    }

    public void updateOauth(String new_oauth) {
        this.oauth = new_oauth;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateGrade(String name) {
        this.grade = name;
    }
}
