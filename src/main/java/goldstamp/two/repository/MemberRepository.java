package goldstamp.two.repository;

import goldstamp.two.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {


    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.loginId = :loginId")
    Member getWithRoles(@Param("loginId") String loginId);
}

