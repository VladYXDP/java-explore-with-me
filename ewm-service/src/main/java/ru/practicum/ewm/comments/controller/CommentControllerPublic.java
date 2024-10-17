package ru.practicum.ewm.comments.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentControllerPublic {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping("/event/{eventId}")
    List<CommentDto> getComments(@PathVariable Long eventId,
                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                 @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        return commentMapper.toCommentDto(commentService.getComments(eventId, from, size));
    }

    @GetMapping("/{commentId}")
    CommentDto getCommentById(@PathVariable Long commentId) {
        return commentMapper.toCommentDto(commentService.getCommentById(commentId));
    }
}
