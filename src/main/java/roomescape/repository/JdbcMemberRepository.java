package roomescape.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> memberRowMapper = (resultSet, rowNum) -> new Member(
            resultSet.getLong("id"),
            resultSet.getString("login_id"),
            resultSet.getString("password"),
            resultSet.getString("name"),
            Role.valueOf(resultSet.getString("role"))
    );

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Member insert(Member member) {
        String sql = "INSERT INTO member(login_id, password, name, role) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, member.getLoginId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getRole().name());
            return pstmt;
        }, keyHolder);

        Long memberId = keyHolder.getKey().longValue();
        return member.withMemberId(memberId);
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT id, login_id, password, name, role FROM member ORDER BY id;";
        return jdbcTemplate.query(sql, memberRowMapper);
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        String sql = "SELECT id, login_id, password, name, role FROM member WHERE id = ?;";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper, memberId);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        String sql = "SELECT id, login_id, password, name, role FROM member WHERE login_id = ?;";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper, loginId);
        return result.stream().findAny();
    }

    @Override
    public void updateRole(Long memberId, Role role) {
        jdbcTemplate.update("UPDATE member SET role = ? WHERE id = ?;", role.name(), memberId);
    }
}
