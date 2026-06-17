package roomescape.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DuplicateKeyException;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomescapeException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    private final MemberRepository memberRepository = mock();
    private final MemberService memberService = new MemberService(memberRepository);

    @Test
    void 회원가입_성공() {
        // given
        String loginId = "gugu";
        String password = "password";
        String name = "구구";
        Member savedMember = new Member(1L, loginId, password, name);

        when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.empty());
        when(memberRepository.insert(any(Member.class)))
                .thenReturn(savedMember);

        // when
        Member result = memberService.join(loginId, password, name);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        assertThat(result).isEqualTo(savedMember);
        verify(memberRepository, times(1)).findByLoginId(loginId);
        verify(memberRepository, times(1)).insert(captor.capture());
        verifyNoMoreInteractions(memberRepository);

        Member capturedMember = captor.getValue();
        assertAll(
                () -> assertThat(capturedMember.getMemberId()).isNull(),
                () -> assertThat(capturedMember.getLoginId()).isEqualTo(loginId),
                () -> assertThat(capturedMember.getPassword()).isEqualTo(password),
                () -> assertThat(capturedMember.getName()).isEqualTo(name));
    }

    @Test
    void 중복_loginId이면_예외() {
        // given
        String loginId = "gugu";
        when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.of(new Member(1L, loginId, "password", "구구")));

        // when & then
        assertThatThrownBy(() -> memberService.join(loginId, "new-password", "포비"))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE)
                .hasMessage("이미 존재하는 로그인 ID입니다.");

        verify(memberRepository, times(1)).findByLoginId(loginId);
        verify(memberRepository, never()).insert(any(Member.class));
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void 저장시_loginId_중복이_발생하면_예외() {
        // given
        String loginId = "gugu";
        when(memberRepository.findByLoginId(loginId))
                .thenReturn(Optional.empty());
        when(memberRepository.insert(any(Member.class)))
                .thenThrow(new DuplicateKeyException("duplicate login_id"));

        // when & then
        assertThatThrownBy(() -> memberService.join(loginId, "password", "구구"))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESOURCE)
                .hasMessage("이미 존재하는 로그인 ID입니다.");

        verify(memberRepository, times(1)).findByLoginId(loginId);
        verify(memberRepository, times(1)).insert(any(Member.class));
        verifyNoMoreInteractions(memberRepository);
    }

    @Test
    void 관리자가_회원을_승격한다() {
        // given
        Member admin = new Member(1L, "admin", "password", "관리자", Role.ADMIN);
        Member target = new Member(2L, "gugu", "password", "구구", Role.USER);
        when(memberRepository.findByMemberId(1L)).thenReturn(Optional.of(admin));
        when(memberRepository.findByMemberId(2L)).thenReturn(Optional.of(target));

        // when
        Member result = memberService.changeRole(1L, 2L, Role.MANAGER);

        // then
        assertThat(result.getRole()).isEqualTo(Role.MANAGER);
        verify(memberRepository, times(1)).updateRole(2L, Role.MANAGER);
    }

    @Test
    void 관리자가_아니면_등급을_변경할_수_없다() {
        // given
        Member manager = new Member(1L, "manager", "password", "매니저", Role.MANAGER);
        when(memberRepository.findByMemberId(1L)).thenReturn(Optional.of(manager));

        // when & then
        assertThatThrownBy(() -> memberService.changeRole(1L, 2L, Role.MANAGER))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FORBIDDEN_RESOURCE);

        verify(memberRepository, never()).updateRole(any(), any());
    }

    @Test
    void ADMIN_권한은_부여할_수_없다() {
        // given
        Member admin = new Member(1L, "admin", "password", "관리자", Role.ADMIN);
        when(memberRepository.findByMemberId(1L)).thenReturn(Optional.of(admin));

        // when & then
        assertThatThrownBy(() -> memberService.changeRole(1L, 2L, Role.ADMIN))
                .isInstanceOf(RoomescapeException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_INPUT);

        verify(memberRepository, never()).updateRole(any(), any());
    }
}
