package goldstamp.two.dto;

import goldstamp.two.domain.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemoResponseDto {
    private Long id;
    private Long memberId;
    private Long diseaseId; // Nullable
    private String memoName;
    private String memoContent;
    private LocalDateTime reminderTime;

    public static MemoResponseDto fromEntity(Memo memo) {
        return MemoResponseDto.builder()
                .id(memo.getId())
                .memberId(memo.getMember().getId())
                .diseaseId(memo.getDisease() != null ? memo.getDisease().getId() : null)
                .memoName(memo.getMemoName())
                .memoContent(memo.getMemoContent())
                .reminderTime(memo.getReminderTime())
                .build();
    }
}