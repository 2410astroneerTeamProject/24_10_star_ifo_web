package com.teamname.astroneer.star_info_web.security;

import com.teamname.astroneer.star_info_web.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final Member member;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + member.getAuthLevel()); // 권한을 Member의 authLevel로 결정
    }

    @Override
    public String getName() {
        return member.getNickname() != null ? member.getNickname() : member.getUName(); // 닉네임이 없을 경우 UName 반환
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public String getPassword() {
        return null; // 패스워드는 사용하지 않음
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(member.getDelStatus()); // 탈퇴 여부에 따라 계정 활성화 여부 결정
    }

    public String getEmail() {
        return member.getEmail();
    }
}