package com.community.weare.Services;

import com.community.weare.Models.SkillCategory;
import com.community.weare.Repositories.SkillCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public SkillCategory create(SkillCategory category) {
        return null;
    }

    @Override
    public SkillCategory update(SkillCategory category) {
        return null;
    }

    @Override
    public void delete(SkillCategory category) {

    }

    @Override
    public List<SkillCategory> getAll() {
        return null;
    }

    @Override
    public Optional<SkillCategory> getByById(int id) {
        return Optional.empty();
    }
}
