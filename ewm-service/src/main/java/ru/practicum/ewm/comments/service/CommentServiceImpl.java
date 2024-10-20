package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.entity.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.entity.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.ForbiddenException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.users.entity.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public Comment addComment(Long userId, Long eventId, Comment comment) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден!"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено!"));
        comment.setAuthor(user);
        comment.setEvent(event);
        return commentRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long userId, Long eventId, Long commentId, Comment comment) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден!"));
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено!"));
        Comment currentComment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий " + commentId + " не найден!"));
        if (currentComment.getAuthor().equals(user)) {
            currentComment.setText(comment.getText());
            currentComment.setEdited(comment.getEdited());
        } else {
            throw new ForbiddenException("Пользователь " + user.getId() + " не может обновить комментарий!");
        }
        return commentRepository.save(currentComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден!"));
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("created"));
        return commentRepository.findAllByAuthorId(userId, pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Comment> getComments(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено!"));
        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("created"));
        return commentRepository.findAllByEventId(eventId, pageRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий " + commentId + " не найден!"));
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден!"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий " + commentId + " не найден!"));
        if (comment.getAuthor().equals(user)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new ForbiddenException("Пользователь " + user.getId() + " не может удалить комментарий " + comment.getId());
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
