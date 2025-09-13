package core.service;

import core.dto.post.PostPendingDto;
import core.dto.post.PostPublishedDto;
import core.dto.post.PostSuggestionDto;
import core.entity.Post;
import core.entity.User;
import core.repository.PostRepository;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class PostServiceTest {
    private static final String TEST_TITLE = "test_title", TEST_TEXT = "test_text";
    private static final Long TEST_ID = 0L;
    private static final String TEST_AUTHOR_USERNAME = "test_author", TEST_REVIEWER_USERNAME = "test_reviewer";
    private PostRepository postRepository;
    private AuthService authService;
    private PostService postService;
    private User author, reviewer;
    private Post post;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        authService = mock(AuthService.class);
        postService = new PostService(postRepository, authService);

        author = new User();
        author.setUsername(TEST_AUTHOR_USERNAME);

        reviewer = new User();
        reviewer.setUsername(TEST_REVIEWER_USERNAME);

        post = new Post();
        post.setId(TEST_ID);
        post.setTitle(TEST_TITLE);
        post.setText(TEST_TEXT);
        post.setAuthor(author);
        post.setReviewer(reviewer);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnPendingPostsWhenGetAllPending() {
        post.setStatus(Post.Status.PENDING);

        when(postRepository.findAllByStatus(Post.Status.PENDING)).thenReturn(List.of(post));

        List<PostPendingDto> pendingPosts = postService.getAllPending();
        verify(postRepository, times(1)).findAllByStatus(Post.Status.PENDING);
        assertEquals(1, pendingPosts.size());

        PostPendingDto pendingPost = pendingPosts.get(0);
        assertEquals(post.getId(), pendingPost.id());
        assertEquals(post.getTitle(), pendingPost.title());
        assertEquals(post.getText(), pendingPost.text());
        assertEquals(post.getAuthor().getUsername(), pendingPost.authorUsername());
    }

    @Test
    void shouldReturnPublishedPostsWhenGetAllPublished() {
        post.setStatus(Post.Status.PUBLISHED);

        when(postRepository.findAllByStatus(Post.Status.PUBLISHED)).thenReturn(List.of(post));

        List<PostPublishedDto> publishedPosts = postService.getAllPublished();
        verify(postRepository, times(1)).findAllByStatus(Post.Status.PUBLISHED);
        assertEquals(publishedPosts.size(), 1);

        PostPublishedDto publishedPost = publishedPosts.get(0);
        assertEquals(post.getTitle(), publishedPost.title());
        assertEquals(post.getText(), publishedPost.text());
        assertEquals(post.getAuthor().getUsername(), publishedPost.authorUsername());
        assertEquals(post.getReviewer().getUsername(), publishedPost.reviewerUsername());
    }

    @Test
    void shouldSaveNewPostWithPendingStatusWhenSuggest() {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(author);
        when(authService.getAuthentication()).thenReturn(auth);

        PostSuggestionDto suggestion = new PostSuggestionDto(TEST_TITLE, TEST_TEXT);
        postService.suggest(suggestion);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(authService, times(1)).getAuthentication();
        verify(postRepository, times(1)).save(postCaptor.capture());

        Post suggested = postCaptor.getValue();
        assertEquals(suggestion.title(), suggested.getTitle());
        assertEquals(suggestion.text(), suggested.getText());
        assertEquals(Post.Status.PENDING, suggested.getStatus());
        assertEquals(author, suggested.getAuthor());
        assertNull(suggested.getReviewer());
    }

    @Test
    void shouldSetStatusAndReviewerWhenPublishExistingPost() {
        post.setStatus(Post.Status.PENDING);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(reviewer);
        when(authService.getAuthentication()).thenReturn(auth);

        when(postRepository.findById(TEST_ID)).thenReturn(Optional.of(post));

        postService.publish(TEST_ID);
        verify(authService, times(1)).getAuthentication();
        verify(postRepository, times(1)).save(post);

        assertEquals(Post.Status.PUBLISHED, post.getStatus());
        assertEquals(reviewer, post.getReviewer());
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenPublishNonExistingPost() {
        when(postRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        assertThrowsExactly(EntityNotFoundException.class, () -> postService.publish(TEST_ID));
    }
}
