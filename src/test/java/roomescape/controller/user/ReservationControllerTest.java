package roomescape.controller.user;

import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import roomescape.auth.SessionConstants;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationSlot;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.member.Member;
import roomescape.service.AuthService;
import roomescape.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private AuthService authService;

    @Test
    void 사용자_예약을_생성한다() throws Exception {
        given(authService.findLoginMember(1L))
                .willReturn(new Member(1L, "brown", "password", "브라운"));
        given(reservationService.createByUser(
                eq(1L),
                eq("브라운"),
                eq(LocalDate.of(2099, 1, 1)),
                eq(1L),
                eq(1L),
                any(LocalDateTime.class)))
                .willReturn(reservation());

        mockMvc.perform(post("/reservations")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservations/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("브라운"))
                .andExpect(jsonPath("$.date").value("2099-01-01"))
                .andExpect(jsonPath("$.time.id").value(1))
                .andExpect(jsonPath("$.time.startAt").value("10:00:00"))
                .andExpect(jsonPath("$.theme.id").value(1))
                .andExpect(jsonPath("$.theme.name").value("테마"));

        verify(reservationService, times(1)).createByUser(
                eq(1L),
                eq("브라운"),
                eq(LocalDate.of(2099, 1, 1)),
                eq(1L),
                eq(1L),
                any(LocalDateTime.class));
        verify(authService, times(1)).findLoginMember(1L);
        verifyNoMoreInteractions(reservationService, authService);
    }

    @Test
    void 사용자_본인_예약을_조회한다() throws Exception {
        given(reservationService.findByMemberId(eq(1L)))
                .willReturn(List.of(reservation()));

        mockMvc.perform(get("/reservations")
                        .with(loginMember()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("브라운"))
                .andExpect(jsonPath("$[0].date").value("2099-01-01"))
                .andExpect(jsonPath("$[0].time.id").value(1))
                .andExpect(jsonPath("$[0].time.startAt").value("10:00:00"))
                .andExpect(jsonPath("$[0].theme.id").value(1))
                .andExpect(jsonPath("$[0].theme.name").value("테마"));

        verify(reservationService, times(1)).findByMemberId(1L);
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 사용자_본인_예약을_취소한다() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/reservations/{id}", id)
                        .with(loginMember()))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).deleteByUser(eq(id), eq(1L), any(LocalDateTime.class));
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 사용자_본인_예약_취소시_id가_양수가_아니면_에러_응답() throws Exception {
        mockMvc.perform(delete("/reservations/0")
                        .with(loginMember())
                        .param("name", "브라운"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").value("id는 양수이어야 합니다."));

        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 사용자_본인_예약을_변경한다() throws Exception {
        Long id = 1L;
        given(reservationService.updateByUser(
                eq(id),
                eq(1L),
                eq(LocalDate.of(2099, 1, 2)),
                eq(2L),
                any(LocalDateTime.class)))
                .willReturn(updatedReservation());

        mockMvc.perform(put("/reservations/{id}", id)
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("브라운"))
                .andExpect(jsonPath("$.date").value("2099-01-02"))
                .andExpect(jsonPath("$.time.id").value(2))
                .andExpect(jsonPath("$.time.startAt").value("12:00:00"))
                .andExpect(jsonPath("$.theme.id").value(1))
                .andExpect(jsonPath("$.theme.name").value("테마"));

        verify(reservationService, times(1)).updateByUser(
                eq(id),
                eq(1L),
                eq(LocalDate.of(2099, 1, 2)),
                eq(2L),
                any(LocalDateTime.class));
        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 사용자_본인_예약_변경시_시간_id가_유효하지_않으면_에러_응답() throws Exception {
        String request = """
                {
                  "date": "2099-01-02",
                  "timeId": 0
                }
                """;

        mockMvc.perform(put("/reservations/1")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").value("timeId는 양수이어야 합니다."));

        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 사용자_본인_예약_변경시_변경할_값이_없으면_에러_응답() throws Exception {
        String request = """
                {}
                """;

        mockMvc.perform(put("/reservations/1")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").value("변경할 날짜 또는 시간이 필요합니다."));

        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 유효하지_않은_입력값이면_에러_응답() throws Exception {
        String request = """
                {
                  "date": "2099-01-01",
                  "timeId": 0,
                  "themeId": 1
                }
                """;

        mockMvc.perform(post("/reservations")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").value("timeId는 양수이어야 합니다."));

        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 요청_본문_형식이_올바르지_않으면_에러_응답() throws Exception {
        String request = """
                {
                  "date": "2099-01-01",
                  "timeId": "abc",
                  "themeId": 1
                }
                """;

        mockMvc.perform(post("/reservations")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
                .andExpect(jsonPath("$.detail").value("요청 본문 형식이 올바르지 않습니다."));

        verifyNoMoreInteractions(reservationService);
    }

    @Test
    void 일시적_DB_실패가_발생하면_재시도_가능한_에러_응답() throws Exception {
        given(authService.findLoginMember(1L))
                .willReturn(new Member(1L, "brown", "password", "브라운"));
        given(reservationService.createByUser(
                eq(1L),
                eq("브라운"),
                eq(LocalDate.of(2099, 1, 1)),
                eq(1L),
                eq(1L),
                any(LocalDateTime.class)))
                .willThrow(new CannotAcquireLockException("lock timeout"));

        mockMvc.perform(post("/reservations")
                        .with(loginMember())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("TEMPORARY_UNAVAILABLE"))
                .andExpect(jsonPath("$.detail").value("요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요."));

        verify(reservationService, times(1)).createByUser(
                eq(1L),
                eq("브라운"),
                eq(LocalDate.of(2099, 1, 1)),
                eq(1L),
                eq(1L),
                any(LocalDateTime.class));
        verify(authService, times(1)).findLoginMember(1L);
        verifyNoMoreInteractions(reservationService, authService);
    }

    @Test
    void 로그인하지_않으면_사용자_예약_요청을_차단한다() throws Exception {
        mockMvc.perform(get("/reservations")
                        .param("name", "브라운"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.detail").value("인증에 실패했습니다."));

        verifyNoMoreInteractions(reservationService);
    }

    private String validRequest() {
        return """
                {
                  "date": "2099-01-01",
                  "timeId": 1,
                  "themeId": 1
                }
                """;
    }

    private String updateRequest() {
        return """
                {
                  "date": "2099-01-02",
                  "timeId": 2
                }
                """;
    }

    private Reservation reservation() {
        ReservationTime time = new ReservationTime(1L, LocalTime.of(10, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        return new Reservation(1L, "브라운", new ReservationSlot(LocalDate.of(2099, 1, 1), time, theme));
    }

    private Reservation updatedReservation() {
        ReservationTime time = new ReservationTime(2L, LocalTime.of(12, 0));
        Theme theme = new Theme(1L, "테마", "설명", "썸네일");
        return new Reservation(1L, "브라운", new ReservationSlot(LocalDate.of(2099, 1, 2), time, theme));
    }

    private RequestPostProcessor loginMember() {
        return request -> {
            request.getSession().setAttribute(SessionConstants.LOGIN_MEMBER_ID, 1L);
            return request;
        };
    }
}
