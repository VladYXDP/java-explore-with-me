package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.entity.Comment;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    @Override
    public Comment addComment(Long userId, Long eventId, Comment comment) {
        return null;
    }

    @Override
    public Comment updateComment(Long userId, Long eventId, Long commentId, Comment comment) {
        return null;
    }

    @Override
    public List<Comment> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public List<Comment> getComments(Long eventId, Integer from, Integer size) {
        return null;
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return null;
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {

    }

    @Override
    public void deleteComment(Long commentId) {

    }
}
