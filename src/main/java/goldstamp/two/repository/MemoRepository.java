package goldstamp.two.repository;

import goldstamp.two.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying; // @Modifying 임포트 추가
import org.springframework.data.jpa.repository.Query; // @Query 임포트 추가

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @EntityGraph(attributePaths = {"member", "disease"})
    List<Memo> findByMember_Id(Long memberId);

    @EntityGraph(attributePaths = {"member", "disease"})
    Optional<Memo> findById(Long id);

    @Modifying // DELETE, UPDATE 쿼리 시 필요
    @Query("delete from Memo m where m.member.id = :memberId")
    void deleteByMember_Id(Long memberId);
}