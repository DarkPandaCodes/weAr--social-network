package com.community.weare.Repositories;

import com.community.weare.Models.Post;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>,
        JpaSpecificationExecutor<Post> {

    List<Post> findAllByUserUsername(Sort sort, String userName);

    Slice<Post> findAllByUserUsername(Pageable pageRequest, String userName);
}


