package roomescape.domain.member;

public class Member {

    private static final int MAX_LENGTH = 255;

    private final Long memberId;
    private final String loginId;
    private final String password;
    private final String name;

    public Member(Long memberId, String loginId, String password, String name) {
        validateRequired(loginId, "loginId");
        validateRequired(password, "password");
        validateRequired(name, "name");
        validateMaxLength(loginId, "loginId");
        validateMaxLength(password, "password");
        validateMaxLength(name, "name");

        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }

    public Member withMemberId(Long memberId) {
        return new Member(memberId, loginId, password, name);
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + subjectParticle(fieldName) + " 비어 있을 수 없습니다.");
        }
    }

    private void validateMaxLength(String value, String fieldName) {
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(fieldName + subjectParticle(fieldName) + " 255자를 넘을 수 없습니다.");
        }
    }

    private String subjectParticle(String fieldName) {
        if ("name".equals(fieldName)) {
            return "은";
        }
        return "는";
    }
}
