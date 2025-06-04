package goldstamp.two.service;

import goldstamp.two.domain.Disease;
import goldstamp.two.domain.Member;
import goldstamp.two.domain.Memo;
import goldstamp.two.dto.MemoRequestDto;
import goldstamp.two.repository.DiseaseRepository;
import goldstamp.two.repository.MemberRepositoryClass;
import goldstamp.two.repository.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoService {

    private final MemoRepository memoRepository;
    private final MemberRepositoryClass memberRepository;
    private final DiseaseService diseaseService; // DiseaseService 주입

    @Transactional
    public Long saveMemo(Long memberId, MemoRequestDto requestDto) {
        Member member = memberRepository.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }

        Disease disease = null;
        if (requestDto.getMemoName() != null && !requestDto.getMemoName().trim().isEmpty()) {
            disease = diseaseService.findOrCreateDisease(requestDto.getMemoName());
        }

        Memo memo = Memo.createMemo(
                member,
                disease,
                requestDto.getMemoName(), // memoName은 질병명 또는 직접 입력한 이름
                requestDto.getMemoContent(),
                requestDto.getReminderTime()
        );
        memoRepository.save(memo);
        return memo.getId();
    }

    @Transactional
    public Memo updateMemo(Long memoId, Long memberId, MemoRequestDto requestDto) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("Memo not found with ID: " + memoId));

        // long 타입 비교는 == 연산자 사용
        if (memo.getMember().getId() != memberId) {
            throw new SecurityException("Unauthorized: Member does not own this memo.");
        }

        Disease disease = null;
        if (requestDto.getMemoName() != null && !requestDto.getMemoName().trim().isEmpty()) {
            disease = diseaseService.findOrCreateDisease(requestDto.getMemoName());
        }

        memo.setMemoName(requestDto.getMemoName());
        memo.setMemoContent(requestDto.getMemoContent());
        memo.setReminderTime(requestDto.getReminderTime());
        memo.setDisease(disease); // 연관된 질병 업데이트

        return memo; // @Transactional에 의해 변경 감지 및 저장
    }

    public List<Memo> getMemosByMemberId(Long memberId) {
        return memoRepository.findByMember_Id(memberId);
    }

    public Optional<Memo> getMemoById(Long memoId, Long memberId) {
        Optional<Memo> memo = memoRepository.findById(memoId);
        // memo.get().getMember().getId()는 long 타입이므로, == 연산자 사용.
        // memberId는 Long 타입이므로, 자동 언박싱 되어 비교됩니다.
        if (memo.isPresent() && memo.get().getMember().getId() != memberId) {
            throw new SecurityException("Unauthorized: Member does not own this memo.");
        }
        return memo;
    }

    @Transactional
    public void deleteMemo(Long memoId, Long memberId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("Memo not found with ID: " + memoId));

        // long 타입 비교는 == 연산자 사용
        if (memo.getMember().getId() != memberId) {
            throw new SecurityException("Unauthorized: Member does not own this memo.");
        }
        memoRepository.delete(memo);
    }
}