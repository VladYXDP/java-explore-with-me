package ru.practicum.ewm.comments.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CreateCommentDto;
import ru.practicum.ewm.comments.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CommentMapper {

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
                comment.getEvent(),
                comment.getAuthor(),
                comment.getCreated(),
                comment.getEdited()
        );
    }
}
