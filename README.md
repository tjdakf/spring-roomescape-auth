## 방탈출 API 명세
| 기능              | 메서드 / URL                                      | 요청                               | 응답                                                           | 상태 코드 |
|-----------------|------------------------------------------------|----------------------------------|--------------------------------------------------------------|-------|
| 회원가입           | POST `/members`                                | `{loginId, password, name}`      | `{loginId}`                                                   | 201   |
| 사용자 예약 등록       | POST `/reservations`                           | `{name, date, timeId, themeId}`  | `{id, name, date, time: {id, startAt}, theme: {id, name}}`   | 201   |
| 사용자 본인 예약 조회    | GET `/reservations?name=브라운`                 | —                                | `[{id, name, date, time: {id, startAt}, theme: {id, name}}, ...]` | 200   |
| 사용자 본인 예약 상태 조회 | GET `/reservation-statuses?name=브라운`         | —                                | `[{id, name, date, time: {id, startAt}, theme: {id, name}, status, turn}, ...]` | 200   |
| 사용자 본인 예약 변경    | PUT `/reservations/{id}`                       | `{name, date?, timeId?}`         | `{id, name, date, time: {id, startAt}, theme: {id, name}}`   | 200   |
| 사용자 본인 예약 취소    | DELETE `/reservations/{id}?name=브라운`          | —                                | —                                                            | 204   |
| 사용자 예약 대기 등록    | POST `/waitings`                               | `{name, date, timeId, themeId}`  | `{id, name, date, time: {id, startAt}, theme: {id, name}, turn}` | 201   |
| 사용자 본인 예약 대기 조회 | GET `/waitings?name=브라운`                    | —                                | `[{id, name, date, time: {id, startAt}, theme: {id, name}, turn}, ...]` | 200   |
| 사용자 본인 예약 대기 취소 | DELETE `/waitings/{id}?name=브라운`             | —                                | —                                                            | 204   |
| 관리자 예약 조회       | GET `/admin/reservations`                      | —                                | `[{id, name, date, time: {id, startAt}, theme: {id, name}}, ...]` | 200   |
| 관리자 예약 상태 조회    | GET `/admin/reservation-statuses?startDate=2026-06-01&endDate=2026-06-30` | —                                | `[{id, name, date, time: {id, startAt}, theme: {id, name}, status, turn}, ...]` | 200   |
| 관리자 예약 등록       | POST `/admin/reservations`                     | `{name, date, timeId, themeId}`  | `{id, name, date, time: {id, startAt}, theme: {id, name}}`   | 201   |
| 관리자 예약 삭제       | DELETE `/admin/reservations/{id}`              | —                                | —                                                            | 204   |
| 관리자 시간 조회       | GET `/admin/times`                             | —                                | `[{id, startAt}, ...]`                                       | 200   |
| 관리자 시간 등록       | POST `/admin/times`                            | `{startAt}`                      | `{id, startAt}`                                              | 201   |
| 관리자 시간 삭제       | DELETE `/admin/times/{id}`                     | —                                | —                                                            | 204   |
| 사용자 테마 조회       | GET `/themes`                                  | —                                | `[{id, name, description, thumbnail}, ...]`                  | 200   |
| 관리자 테마 조회       | GET `/admin/themes`                            | —                                | `[{id, name, description, thumbnail}, ...]`                  | 200   |
| 관리자 테마 등록       | POST `/admin/themes`                           | `{name, description, thumbnail}` | `{id, name, description, thumbnail}`                         | 201   |
| 관리자 테마 삭제       | DELETE `/admin/themes/{id}`                    | —                                | —                                                            | 204   |
| 예약 가능 시간 조회     | GET `/themes/{id}/times?date=2026-05-08`       | —                                | `[{time, available}, ...]`                                   | 200   |
| 인기 테마 상위 10개 조회 | GET `/themes/popular`                          | —                                | `[{id, name, description, thumbnail, reservationCount}, ...]` | 200   |

## 에러 응답 명세

모든 에러 응답은 JSON 객체로 반환한다.

```json
{
  "code": "ERROR_CODE",
  "detail": "에러 상세 설명"
}
```

`code`는 클라이언트가 분기하기 위한 안정적인 값이다. `detail`은 API 소비자가 에러 원인을 이해하기 위한 설명이며, 클라이언트는 `detail`이 아닌 `code`를 기준으로 처리한다.

| error code | detail | 상태 코드 | 상황 |
|------------|--------|----------|------|
| INVALID_INPUT | name은 비어 있을 수 없습니다. | 400 | 요청 본문의 필수 문자열 값이 비어 있음 |
| INVALID_INPUT | name은 255자를 넘을 수 없습니다. | 400 | 요청 본문의 문자열 길이가 허용 범위를 초과함 |
| INVALID_INPUT | loginId는 비어 있을 수 없습니다. | 400 | 회원가입 요청의 로그인 ID가 비어 있음 |
| INVALID_INPUT | loginId는 255자를 넘을 수 없습니다. | 400 | 회원가입 요청의 로그인 ID 길이가 허용 범위를 초과함 |
| INVALID_INPUT | password는 비어 있을 수 없습니다. | 400 | 회원가입 요청의 비밀번호가 비어 있음 |
| INVALID_INPUT | password는 255자를 넘을 수 없습니다. | 400 | 회원가입 요청의 비밀번호 길이가 허용 범위를 초과함 |
| INVALID_INPUT | timeId는 비어 있을 수 없습니다. | 400 | 요청 본문의 필수 ID 값이 누락됨 |
| INVALID_INPUT | timeId는 양수이어야 합니다. | 400 | 요청 본문의 ID 값이 양수가 아님 |
| INVALID_INPUT | themeId는 비어 있을 수 없습니다. | 400 | 요청 본문의 필수 ID 값이 누락됨 |
| INVALID_INPUT | themeId는 양수이어야 합니다. | 400 | 요청 본문의 ID 값이 양수가 아님 |
| INVALID_INPUT | startAt은 비어 있을 수 없습니다. | 400 | 예약 시간 생성 요청의 시작 시간이 누락됨 |
| INVALID_INPUT | description은 255자를 넘을 수 없습니다. | 400 | 테마 설명 길이가 허용 범위를 초과함 |
| INVALID_INPUT | thumbnail은 255자를 넘을 수 없습니다. | 400 | 테마 썸네일 경로 길이가 허용 범위를 초과함 |
| INVALID_INPUT | id는 양수이어야 합니다. | 400 | 경로 변수 ID 값이 양수가 아님 |
| INVALID_INPUT | 요청 본문 형식이 올바르지 않습니다. | 400 | JSON 형식이 잘못됐거나 요청 본문 타입 변환에 실패함 |
| INVALID_INPUT | date 형식이 올바르지 않습니다. | 400 | 요청 파라미터의 날짜 형식이 올바르지 않음 |
| INVALID_INPUT | id 형식이 올바르지 않습니다. | 400 | 경로 변수 ID 형식이 올바르지 않음 |
| INVALID_INPUT | date는 필수입니다. | 400 | 예약 가능 시간 조회 요청의 date 파라미터가 누락됨 |
| INVALID_INPUT | date는 비어 있을 수 없습니다. | 400 | 예약 생성 요청의 date 값이 누락됨 |
| INVALID_INPUT | name는 필수입니다. | 400 | 내 예약·대기 조회 또는 취소 요청의 이름 파라미터가 누락됨 |
| INVALID_INPUT | 변경할 날짜 또는 시간이 필요합니다. | 400 | 예약 변경 요청에 날짜와 시간 ID가 모두 누락됨 |
| INVALID_INPUT | 예약 가능한 시간에는 대기를 신청할 수 없습니다. | 400 | 아직 예약되지 않은 날짜·시간·테마에 대기 신청을 요청함 |
| PAST_SCHEDULE | 이미 지난 시간으로는 예약할 수 없습니다. | 400 | 사용자가 지난 날짜·시간으로 예약 생성·변경을 요청함 |
| PAST_SCHEDULE | 이미 지난 시간으로는 예약 대기를 신청할 수 없습니다. | 400 | 사용자가 지난 날짜·시간으로 예약 대기 생성을 요청함 |
| FORBIDDEN_RESOURCE | 본인의 예약만 변경하거나 취소할 수 있습니다. | 403 | 예약은 존재하지만 요청 이름과 예약 이름이 일치하지 않음 |
| FORBIDDEN_RESOURCE | 본인의 예약 대기만 취소할 수 있습니다. | 403 | 예약 대기는 존재하지만 요청 이름과 예약 대기 이름이 일치하지 않음 |
| NOT_FOUND | 존재하지 않는 예약 시간입니다. | 404 | 존재하지 않는 예약 시간 ID로 요청함 |
| NOT_FOUND | 존재하지 않는 시간입니다. | 404 | 존재하지 않는 예약 시간 ID로 예약 대기 생성을 요청함 |
| NOT_FOUND | 존재하지 않는 테마입니다. | 404 | 존재하지 않는 테마 ID로 요청함 |
| NOT_FOUND | 존재하지 않는 예약입니다. | 404 | 존재하지 않는 예약 ID로 변경·취소를 요청함 |
| NOT_FOUND | 존재하지 않는 예약 대기입니다. | 404 | 존재하지 않는 예약 대기 ID로 취소를 요청함 |
| NOT_FOUND | 존재하지 않는 리소스입니다. | 404 | 존재하지 않는 URL로 요청함 |
| DUPLICATE_RESOURCE | 이미 예약된 시간입니다. | 409 | 같은 날짜·시간·테마에 이미 다른 예약이 존재함 |
| DUPLICATE_RESOURCE | 이미 예약 대기를 신청한 시간입니다. | 409 | 같은 사용자가 같은 날짜·시간·테마에 중복 대기를 요청함 |
| DUPLICATE_RESOURCE | 이미 존재하는 로그인 ID입니다. | 409 | 회원가입 요청의 로그인 ID가 이미 존재함 |
| WAITING_NOT_ALLOWED_FOR_OWN_RESERVATION | 본인이 예약한 시간에는 대기를 신청할 수 없습니다. | 409 | 본인이 이미 예약한 날짜·시간·테마에 대기 신청을 요청함 |
| PAST_RESOURCE_LOCKED | 이미 지난 예약은 변경하거나 취소할 수 없습니다. | 409 | 이미 지난 예약 변경·취소를 요청함 |
| PAST_RESOURCE_LOCKED | 이미 지난 예약 대기는 취소할 수 없습니다. | 409 | 이미 지난 예약 대기 취소를 요청함 |
| UNCHANGED_RESERVATION | 기존 예약과 같은 날짜·시간으로는 변경할 수 없습니다. | 409 | 기존 날짜·시간과 동일한 예약 변경을 요청함 |
| RESOURCE_IN_USE | 예약이 존재하는 시간은 삭제할 수 없습니다. | 409 | 예약이 연결된 예약 시간 삭제를 요청함 |
| RESOURCE_IN_USE | 예약이 존재하는 테마는 삭제할 수 없습니다. | 409 | 예약이 연결된 테마 삭제를 요청함 |
| TEMPORARY_UNAVAILABLE | 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요. | 503 | 락 대기 실패 등 일시적인 DB 처리 실패가 발생함 |
| INTERNAL_SERVER_ERROR | 서버에 문제가 발생했습니다. | 500 | 예상하지 못한 서버 오류가 발생함 |

---

## 구현할 기능 목록

### 회원가입
- [x] 회원 도메인과 메모리 저장소 추가
  - `Member`는 `memberId`, `loginId`, `password`, `name`을 가짐
  - `memberId`는 내부 식별자, `loginId`는 로그인 ID로 구분
  - `MemberRepository` 인터페이스 도입
  - `MemoryMemberRepository`로 구현
  - `Member` 저장 기능 추가
  - `loginId`로 회원 조회 기능 추가
  - `memberId`로 회원 조회 기능 추가

- [x] 회원가입 서비스 추가
  - 회원가입 요청 값으로 `loginId`, `password`, `name`을 받음
  - `loginId`로 기존 회원을 조회해 중복 여부를 판단
  - 중복 `loginId`면 예외 발생
  - 중복이 아니면 `Member` 생성 후 저장

- [x] 회원가입 컨트롤러 추가
  - `POST /members` 요청을 받음
  - 요청 DTO로 `loginId`, `password`, `name`을 받음
  - 회원가입 서비스 호출
  - 성공 시 `201 Created` 반환

### 로그인
- [x] 로그인 서비스 추가
  - 로그인 요청 값으로 `loginId`, `password`를 받음
  - `loginId`로 회원 조회
  - 회원이 없으면 로그인 실패 예외 발생
  - 비밀번호가 일치하지 않으면 로그인 실패 예외 발생
  - 성공하면 로그인한 `Member` 반환

- [x] 로그인 컨트롤러 추가
  - `POST /login` 요청을 받음
  - 요청 DTO로 `loginId`, `password`를 받음
  - 로그인 서비스 호출
  - 로그인 성공 시 세션에 `loginMemberId` 저장
  - 세션에는 `loginId`가 아니라 내부 식별자인 `memberId` 저장
  - 성공 시 `200 OK` 반환

- [ ] 현재 로그인 사용자 조회 API 추가
  - `GET /me` 요청을 받음
  - 세션의 `loginMemberId`로 현재 로그인 사용자를 조회
  - 로그인 상태이면 사용자 정보 반환
  - 응답 DTO로 `loginId`, `name`을 반환
  - 로그인 상태가 아니면 `401 Unauthorized` 반환
  - 홈 화면에서는 이 API를 통해 로그인 여부를 판단할 수 있음

### 로그아웃
- [ ] 로그아웃 컨트롤러 추가
  - `POST /logout` 요청을 받음
  - 현재 세션이 있으면 `invalidate()`
  - 현재 세션이 없어도 실패로 보지 않음
  - 성공 시 `204 No Content` 반환

### 예외 처리
- [ ] 회원/인증 예외 응답 정리
  - 중복 `loginId` 예외 추가
  - 로그인 실패 예외 추가
  - 로그인 실패는 `401 Unauthorized`로 응답
  - 중복 `loginId`는 `409 Conflict`로 응답
  - 기존 `GlobalExceptionHandler` 응답 형식과 맞춤
