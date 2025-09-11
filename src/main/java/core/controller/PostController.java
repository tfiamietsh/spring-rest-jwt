package core.controller;

import core.dto.post.PostPublishedDto;
import core.dto.post.PostSuggestionDto;
import core.dto.post.PostPendingDto;
import core.service.PostService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostPublishedDto>> getPosts() {
        List<PostPublishedDto> publishedPosts = postService.getAllPublished();

        if (publishedPosts.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(postService.getAllPublished());
    }

    @PostMapping("/suggest")
    public ResponseEntity<String> postSuggest(@Valid @RequestBody PostSuggestionDto postSuggestionDto) {
        postService.suggest(postSuggestionDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("Пост успешно предложен");
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PostPendingDto>> getPending() {
        List<PostPendingDto> pendingPosts = postService.getAllPending();

        if (pendingPosts.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(pendingPosts);
    }

    @PatchMapping("/publish/{id}")
    public ResponseEntity<String> patchPublish(@PathVariable Long id) {
        postService.publish(id);

        return ResponseEntity.ok("Пост успешно опубликован");
    }
}
