package roomescape.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginMemberInfo;
import roomescape.controller.dto.response.ReservationStatusResponse;
import roomescape.service.ReservationLookupService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/reservation-statuses")
public class ReservationStatusController {

    private final ReservationLookupService reservationLookupService;

    public ReservationStatusController(ReservationLookupService reservationLookupService) {
        this.reservationLookupService = reservationLookupService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationStatusResponse>> getReservationStatuses(
            @LoginMember LoginMemberInfo loginMemberInfo
    ) {
        List<ReservationStatusResponse> results = reservationLookupService.findByMemberId(loginMemberInfo.memberId())
                .stream().map(ReservationStatusResponse::from).toList();
        return ResponseEntity.ok(results);
    }
}
