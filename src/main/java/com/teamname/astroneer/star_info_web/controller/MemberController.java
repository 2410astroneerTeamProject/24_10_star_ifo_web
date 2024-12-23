package com.teamname.astroneer.star_info_web.controller;

import com.teamname.astroneer.star_info_web.config.redis.service.RedisRefreshTokenService;
import com.teamname.astroneer.star_info_web.dto.MemberDetailDTO;
import com.teamname.astroneer.star_info_web.entity.Member;
import com.teamname.astroneer.star_info_web.jwt.JwtUtil;
import com.teamname.astroneer.star_info_web.mapper.MemberMapper;
import com.teamname.astroneer.star_info_web.security.CustomOAuth2User;
import com.teamname.astroneer.star_info_web.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;


    @GetMapping("/me")
    public ResponseEntity<MemberDetailDTO> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                Optional<Member> memberOptional = memberService.findByEmail(Objects.requireNonNull(oAuth2User.getAttribute("email")));
                if (memberOptional.isPresent()) {
                    Member member = memberOptional.get();
                    MemberDetailDTO memberDetailDTO = memberMapper.toMemberDetailDTO(member);
                    return ResponseEntity.ok(memberDetailDTO);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    // 사용자 상세 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<MemberDetailDTO> getUserDetail(@PathVariable long userId) {
        MemberDetailDTO userDetail = memberService.getMemberDetail(userId);
        if (userDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDetail);
    }

    // 즐겨찾기 위치 설정
    @PostMapping("/{userId}/favorite-location/{locationId}")
    public ResponseEntity<MemberDetailDTO> updateFavoriteLocation(
            @PathVariable long userId,
            @PathVariable long locationId) {
        // 즐겨찾기 위치 업데이트 로직
        memberService.updateFavoriteLocation(userId, locationId);

        // 업데이트 이후 사용자 정보를 다시 조회하여 최신화된 DTO를 반환
        MemberDetailDTO updatedUserDetail = memberService.getMemberDetail(userId);
        return ResponseEntity.ok(updatedUserDetail);
    }

    // 위치 설명 업데이트
    @PatchMapping("/location/{locationId}/description")
    public ResponseEntity<MemberDetailDTO> updateLocationDescription(
            @PathVariable long locationId,
            @RequestBody Map<String, String> body) {
        String description = body.get("description");

        System.out.println("Received description: " + description);

        // 위치 설명 업데이트 로직
        memberService.updateLocationDescription(locationId, description);

        // 업데이트된 위치 ID로 사용자 ID를 조회하고, 최신화된 사용자 DTO를 반환
        long userId = memberService.getUserIdByLocationId(locationId);  // locationId로 사용자 ID를 조회
        MemberDetailDTO updatedUserDetail = memberService.getMemberDetail(userId);
        return ResponseEntity.ok(updatedUserDetail);
    }

    // 로그인 상태 확인 API
    @GetMapping("/check-auth")
    public boolean checkAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }
}
