package com.zzw.zzw_final.Dto.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JoinColumn(name = "gradeList_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private GradeList gradeList;

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public Grade(Member member, GradeList gradeList) {
        this.gradeList = gradeList;
        this.member = member;
    }
}
