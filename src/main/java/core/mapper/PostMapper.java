package core.mapper;

import core.dto.post.PostPublishedDto;
import core.dto.post.PostSuggestionDto;
import core.dto.post.PostPendingDto;
import core.entity.Post;
import core.entity.User;

public class PostMapper {
    public static PostPendingDto toPendingDto(Post post) {
        return new PostPendingDto(post.getId(), post.getTitle(), post.getText(), post.getAuthor().getUsername());
    }

    public static PostPublishedDto toPublishedDto(Post post) {
        return new PostPublishedDto(post.getTitle(), post.getText(), post.getAuthor().getUsername(),
                post.getReviewer().getUsername());
    }

    public static Post toEntity(PostSuggestionDto postSuggestionDto, Post.Status status, User author, User reviewer) {
        Post post = new Post();

        post.setTitle(postSuggestionDto.title());
        post.setText(postSuggestionDto.text());
        post.setStatus(status);
        post.setAuthor(author);
        post.setReviewer(reviewer);
        return post;
    }
}
