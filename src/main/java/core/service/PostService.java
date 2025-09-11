package core.service;

import core.dto.post.PostPublishedDto;
import core.dto.post.PostSuggestionDto;
import core.dto.post.PostPendingDto;
import core.entity.Post;
import core.entity.User;
import core.mapper.PostMapper;
import core.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
    }

    @Transactional
    public List<PostPendingDto> getAllPending() {
        return postRepository.findAllByStatus(Post.Status.PENDING).stream()
                .map(PostMapper::toPendingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostPublishedDto> getAllPublished() {
        return postRepository.findAllByStatus(Post.Status.PUBLISHED).stream()
                .map(PostMapper::toPublishedDto)
                .collect(Collectors.toList());
    }

    public void suggest(PostSuggestionDto postSuggestionDto) {
        User author = (User) authService.getAuthentication().getPrincipal();
        Post post = PostMapper.toEntity(postSuggestionDto, Post.Status.PENDING, author, null);

        postRepository.save(post);
    }

    @Transactional
    public void publish(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));
        User reviewer = (User) authService.getAuthentication().getPrincipal();

        post.setStatus(Post.Status.PUBLISHED);
        post.setReviewer(reviewer);

        postRepository.save(post);
    }
}
