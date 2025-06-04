package goldstamp.two.repository;

import goldstamp.two.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    @EntityGraph(attributePaths = {"member", "disease"})
    List<Memo> findByMember_Id(Long memberId);

    @EntityGraph(attributePaths = {"member", "disease"})
    Optional<Memo> findById(Long id);
}