package roomescape.domain.member;

public class Member {

    private static final int MAX_LENGTH = 255;

    private final Long memberId;
    private final String loginId;
    private final String password;
    private final String name;
    private final Role role;

    public Member(Long memberId, String loginId, String password, String name) {
        this(memberId, loginId, password, name, Role.USER);
    }

    public Member(Long memberId, String loginId, String password, String name, Role role) {
        validateRequired(loginId, "loginId");
        validateRequired(password, "password");
        validateRequired(name, "name");
        validateMaxLength(loginId, "loginId");
        validateMaxLength(password, "password");
        validateMaxLength(name, "name");
        validateRole(role);

        this.memberId = memberId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean hasPassword(String password) {
        return this.password.equals(password);
    }

    public Member withMemberId(Long memberId) {
        return new Member(memberId, loginId, password, name, role);
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

    private void validateRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("role은 비어 있을 수 없습니다.");
        }
    }

    private String subjectParticle(String fieldName) {
        if ("name".equals(fieldName)) {
            return "은";
        }
        return "는";
    }
}
