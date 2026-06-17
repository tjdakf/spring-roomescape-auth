package roomescape.controller.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginMemberInfo;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.controller.dto.request.ReservationUpdateRequest;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.domain.Reservation;
import roomescape.domain.member.Member;
import roomescape.service.AuthService;
import roomescape.service.ReservationService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService service;
    private final AuthService authService;

    public ReservationController(ReservationService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @LoginMember LoginMemberInfo loginMemberInfo,
            @Valid @RequestBody ReservationRequest request
    ) {
        Member member = authService.findLoginMember(loginMemberInfo.memberId());
        Reservation reservation = service.createByUser(
                member.getMemberId(),
                member.getName(),
                request.date(),
                request.timeId(),
                request.themeId(),
                LocalDateTime.now());
        return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
                .body(ReservationResponse.from(reservation));
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservationsByName(
            @LoginMember LoginMemberInfo loginMemberInfo
    ) {
        List<ReservationResponse> reservations = service.findByMemberId(loginMemberInfo.memberId()).stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable @Positive(message = "id는 양수이어야 합니다.") Long id,
            @LoginMember LoginMemberInfo loginMemberInfo
    ) {
        service.deleteByUser(id, loginMemberInfo.memberId(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable @Positive(message = "id는 양수이어야 합니다.") Long id,
            @LoginMember LoginMemberInfo loginMemberInfo,
            @Valid @RequestBody ReservationUpdateRequest request
    ) {
        Reservation reservation = service.updateByUser(
                id,
                loginMemberInfo.memberId(),
                request.date(),
                request.timeId(),
                LocalDateTime.now());
        return ResponseEntity.ok(ReservationResponse.from(reservation));
    }
}
