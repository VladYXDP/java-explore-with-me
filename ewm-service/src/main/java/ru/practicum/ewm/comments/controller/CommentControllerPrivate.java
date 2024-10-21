package ru.practicum.ewm.comments.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CreateCommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/comments")
@RequiredArgsConstructor
public class CommentControllerPrivate {

    private final CommentMapper commentMapper;
    private final CommentService commentService;

    @PostMapping("/{userId}/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid CreateCommentDto dto) {
        return commentMapper.toCommentDto(commentService.addComment(userId, eventId, commentMapper.toComment(dto)));
    }

    @PatchMapping("/{userId}/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @RequestBody @Valid CreateCommentDto dto) {
        return commentMapper.toCommentDto(commentService.updateComment(userId, commentId, commentMapper.toUpdateComment(dto)));
    }

    @GetMapping("/{userId}")
    List<CommentDto> getCommentsByAuthor(@PathVariable @Positive Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentMapper.toCommentDto(commentService.getCommentsByAuthor(userId, from, size));
    }

    @DeleteMapping("/{userId}/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}
