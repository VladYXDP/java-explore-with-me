package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.entity.Comment;

import java.util.List;

public interface CommentService {

    Comment addComment(Long userId, Long eventId, Comment comment);

    Comment updateComment(Long userId, Long commentId, Comment comment);

    List<Comment> getCommentsByAuthor(Long userId, Integer from, Integer size);

    List<Comment> getComments(Long eventId, Integer from, Integer size);

    Comment getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);

    void deleteComment(Long commentId);
}
