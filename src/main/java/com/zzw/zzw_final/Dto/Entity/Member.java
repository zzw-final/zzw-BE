package com.zzw.zzw_final.Dto.Entity;

import com.sun.istack.NotNull;
import com.zzw.zzw_final.Dto.GoogleLoginDto;
import com.zzw.zzw_final.Dto.OauthUserDto;
import com.zzw.zzw_final.Dto.Request.SignupRequestDto;
import lombok.*;
import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChatRead> chatReads;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChatRoomOut> chatRoomOuts;

    public Member(SignupRequestDto requestDto, ProfileList profileList){
        this.nickname = requestDto.getNickname();
        this.email = requestDto.getEmail();
        this.oauth = requestDto.getOauth();
        this.grade = "베타테스터";
        this.profile = profileList.getProfile();
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateGrade(String name) {
        this.grade = name;
    }

    public void updateNickname(String nickname) {
        this.nickname= nickname;
    }
}
