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

    public Comment toComment(CreateCommentDto createCommentDto) {
        Comment comment = new Comment();
        comment.setText(createCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream().map(this::toCommentDto).toList();
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                userMapper.toUserShortDto(comment.getAuthor()),
                eventMapper.toEventShortDto(comment.getEvent()),
                comment.getCreated(),
                comment.getEdited()
        );
    }
}
