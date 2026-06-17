package roomescape.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginMemberInfo;
import roomescape.controller.dto.request.ReservationWaitingRequest;
import roomescape.controller.dto.response.ReservationWaitingResponse;
import roomescape.domain.member.Member;
import roomescape.domain.WaitingWithTurn;
import roomescape.service.AuthService;
import roomescape.service.ReservationWaitingService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/waitings")
public class ReservationWaitingController {

    private final ReservationWaitingService service;
    private final AuthService authService;

    public ReservationWaitingController(ReservationWaitingService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ReservationWaitingResponse> createReservationWaiting(
            @LoginMember LoginMemberInfo loginMemberInfo,
            @Valid @RequestBody ReservationWaitingRequest request
    ) {
        Member member = authService.findLoginMember(loginMemberInfo.memberId());
        WaitingWithTurn waitingWithTurn = service.create(
                member.getMemberId(),
                member.getName(),
                request.date(),
                request.timeId(),
                request.themeId(),
                LocalDateTime.now()
        );
        return ResponseEntity.created(URI.create("/waitings/" + waitingWithTurn.waiting().getId()))
                .body(ReservationWaitingResponse.from(waitingWithTurn));
    }

    @GetMapping
    public ResponseEntity<List<ReservationWaitingResponse>> getReservationWaitingsByName(
            @LoginMember LoginMemberInfo loginMemberInfo
    ) {
        List<ReservationWaitingResponse> reservationWaitings = service.findByMemberId(loginMemberInfo.memberId()).stream()
                .map(ReservationWaitingResponse::from)
                .toList();
        return ResponseEntity.ok(reservationWaitings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiting(
            @PathVariable @Positive(message = "id는 양수이어야 합니다.") Long id,
            @LoginMember LoginMemberInfo loginMemberInfo
    ) {
        service.deleteByUser(id, loginMemberInfo.memberId(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }
}
