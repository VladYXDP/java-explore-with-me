package ru.practicum.ewm.comments.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CreateCommentDto;
import ru.practicum.ewm.comments.entity.Comment;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.users.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public Comment toComment(CreateCommentDto dto) {
        return Comment.builder()
                .text(dto.getText())
                .edited(LocalDateTime.now())
                .created(LocalDateTime.now())
                .build();
    }

    public Comment toUpdateComment(CreateCommentDto dto) {
        return Comment.builder()
                .text(dto.getText())
                .edited(LocalDateTime.now())
                .build();
    }

    public List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).toList();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .edited(comment.getEdited())
                .created(comment.getCreated())
                .author(userMapper.toUserShortDto(comment.getAuthor()))
                .event(eventMapper.toEventShortDto(comment.getEvent()))
                .build();
    }
}
