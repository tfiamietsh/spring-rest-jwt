package core.repository;

import core.entity.Post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"author", "reviewer"})
    List<Post> findAllByStatus(Post.Status status);
}
