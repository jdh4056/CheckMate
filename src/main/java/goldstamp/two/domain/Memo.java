package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "memos")
public class Memo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "memo_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id")
    private Disease disease; // Disease 테이블 참조, null 가능

    @Column(name = "memo_name", length = 1000)
    private String memoName; // Disease 테이블의 Name 또는 직접 입력한 질병명

    @Column(name = "memo_content", length = 4000) // 메모 내용은 길 수 있으므로 넉넉하게
    private String memoContent;

    @Column(name = "reminder_time", nullable = true) // 알림 시간, null 가능
    private LocalDateTime reminderTime;

    // 생성자 및 편의 메서드 (필요시 추가)
    public static Memo createMemo(Member member, Disease disease, String memoName, String memoContent, LocalDateTime reminderTime) {
        Memo memo = new Memo();
        memo.setMember(member);
        memo.setDisease(disease);
        memo.setMemoName(memoName);
        memo.setMemoContent(memoContent);
        memo.setReminderTime(reminderTime);
        return memo;
    }
}