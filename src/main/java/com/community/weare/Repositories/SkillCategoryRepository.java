package com.community.weare.Repositories;

import com.community.weare.Models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillCategoryRepository extends JpaRepository<Category,Integer> {
    Optional<Category>findByCategory(String category);


//    boolean exists(SkillCategory category);
}
