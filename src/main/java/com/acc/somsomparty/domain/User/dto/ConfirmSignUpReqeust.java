package com.acc.somsomparty.domain.User.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmSignUpReqeust {
    private String email;
    private String name;
    private String code;
}
