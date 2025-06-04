package goldstamp.two.controller;

import goldstamp.two.domain.Memo;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.dto.MemoRequestDto;
import goldstamp.two.dto.MemoResponseDto;
import goldstamp.two.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/members/{memberId}/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    // 새 메모 저장
    @PostMapping
    public ResponseEntity<Long> createMemo(
            @PathVariable Long memberId,
            @RequestBody MemoRequestDto requestDto,
            @AuthenticationPrincipal MemberDto currentUser
    ) {
        if (!currentUser.getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Long memoId = memoService.saveMemo(memberId, requestDto);
        return ResponseEntity.ok(memoId);
    }

    // 모든 메모 조회
    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> getMemosByMemberId(
            @PathVariable Long memberId,
            @AuthenticationPrincipal MemberDto currentUser
    ) {
        if (!currentUser.getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Memo> memos = memoService.getMemosByMemberId(memberId);
        List<MemoResponseDto> response = memos.stream()
                .map(MemoResponseDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 특정 메모 업데이트
    @PutMapping("/{memoId}")
    public ResponseEntity<MemoResponseDto> updateMemo(
            @PathVariable Long memberId,
            @PathVariable Long memoId,
            @RequestBody MemoRequestDto requestDto,
            @AuthenticationPrincipal MemberDto currentUser
    ) {
        if (!currentUser.getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Memo updatedMemo = memoService.updateMemo(memoId, memberId, requestDto);
            return ResponseEntity.ok(MemoResponseDto.fromEntity(updatedMemo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 특정 메모 삭제
    @DeleteMapping("/{memoId}")
    public ResponseEntity<Void> deleteMemo(
            @PathVariable Long memberId,
            @PathVariable Long memoId,
            @AuthenticationPrincipal MemberDto currentUser
    ) {
        if (!currentUser.getId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            memoService.deleteMemo(memoId, memberId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}