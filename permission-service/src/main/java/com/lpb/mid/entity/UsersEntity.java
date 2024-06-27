package com.lpb.mid.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "USERS")
public class UsersEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private String id;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "PASS_WORD")
    private String passWord;
    @Column(name = "USER_NAME")
    private String userName;
    @Column(name = "STATUS")

    private String status;
    @Column(name = "LOGIN_FAIL")

    private String loginFail;
    @Column(name = "INPUTER")

    private String inputter;
    @Column(name = "CHECKER")

    private String checker;
    @Column(name = "CHANNEL")

    private String channel;
    @Column(name = "WHILE_LIST_IP")

    private String whiteListIp;
    @Column(name = "APP_NAME")

    private String appName;
    @Column(name = "APP_ID")

    private String appId;
    @Column(name = "SECRET_KEY")

    private String secretKey;
    @Column(name = "CREATE_DATE")

    private Instant  createDate;
    @Column(name = "MODIFIER_DATE")

    private Instant  modifierDate;

}
