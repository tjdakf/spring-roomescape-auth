package roomescape.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import roomescape.auth.SessionConstants;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.service.ReservationLookupService;
import roomescape.service.dto.ReservationStatus;
import roomescape.service.dto.Status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationStatusController.class)
class ReservationStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationLookupService reservationLookupService;

    @Test
    void 로그인_사용자의_예약과_예약_대기를_함께_조회한다() throws Exception {
        given(reservationLookupService.findByMemberId(eq(1L)))
                .willReturn(List.of(reservedStatus(), waitingStatus()));

        mockMvc.perform(get("/reservation-statuses")
                        .with(loginMember()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("브라운"))
                .andExpect(jsonPath("$[0].date").value("2099-01-01"))
                .andExpect(jsonPath("$[0].time.id").value(1))
                .andExpect(jsonPath("$[0].time.startAt").value("10:00:00"))
                .andExpect(jsonPath("$[0].theme.id").value(1))
                .andExpect(jsonPath("$[0].theme.name").value("테마"))
                .andExpect(jsonPath("$[0].status").value("RESERVED"))
                .andExpect(jsonPath("$[0].turn").value(nullValue()))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("WAITING"))
                .andExpect(jsonPath("$[1].turn").value(1));

        verify(reservationLookupService, times(1)).findByMemberId(1L);
        verifyNoMoreInteractions(reservationLookupService);
    }

    @Test
    void 로그인하지_않으면_예약_상태_조회_요청을_차단한다() throws Exception {
        mockMvc.perform(get("/reservation-statuses"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.detail").value("인증에 실패했습니다."));

        verifyNoMoreInteractions(reservationLookupService);
    }

    private ReservationStatus reservedStatus() {
        ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        return new ReservationStatus(
                1L,
                "브라운",
                LocalDate.of(2099, 1, 1),
                time,
                theme,
                Status.RESERVED,
                null);
    }

    private ReservationStatus waitingStatus() {
        ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        return new ReservationStatus(
                2L,
                "브라운",
                LocalDate.of(2099, 1, 1),
                time,
                theme,
                Status.WAITING,
                1L);
    }

    private RequestPostProcessor loginMember() {
        return request -> {
            request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);
            return request;
        };
    }
}
