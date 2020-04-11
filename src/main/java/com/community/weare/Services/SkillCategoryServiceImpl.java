package com.community.weare.Services;

import com.community.weare.Models.Category;
import com.community.weare.Repositories.SkillCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service

public class SkillCategoryServiceImpl implements SkillCategoryService {
    private final SkillCategoryRepository categoryRepository;

    @Autowired
    public SkillCategoryServiceImpl(SkillCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category create(Category category) {
        return null;
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public void delete(Category category) {

    }

    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public Optional<Category> getByById(int id) {
        return Optional.empty();
    }

   @Transactional
    @Override
    public Category createIfNotExist(Category category1) {
        boolean exists = categoryRepository.findByCategory(category1.getCategory()).isPresent();
        Category categoryDB = new Category();

        if (exists == true) {
           categoryDB = categoryRepository.findByCategory(category1.getCategory()).get();
        } else {
            categoryDB = categoryRepository.saveAndFlush(category1);
        }
        return categoryDB;
    }

}
