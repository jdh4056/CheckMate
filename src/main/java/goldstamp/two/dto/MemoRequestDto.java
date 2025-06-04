package goldstamp.two.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemoRequestDto {
    private String memoName; // 질병명 또는 직접 입력한 이름
    private String memoContent;
    private LocalDateTime reminderTime;
}