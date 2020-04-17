package com.community.weare.Repositories;

import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>,
        JpaSpecificationExecutor<Post> {

    @Query(value = "SELECT p.postId FROM Post as p join p.likes as l where p.postId = ?1 and l.userId = ?2")
    Integer isLiked(int postId, int userId);
}


