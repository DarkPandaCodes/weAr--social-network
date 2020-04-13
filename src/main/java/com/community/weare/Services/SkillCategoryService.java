package com.community.weare.Services;

import com.community.weare.Models.Category;

import java.util.List;
import java.util.Optional;

public interface SkillCategoryService {
    Category create(Category category);
    Category update(Category category);
    void delete(Category category);
    List<Category> getAll();
    Optional <Category> getByById(int id);
    Category createIfNotExist(Category category1);
    Category getByName(String expertise);
}
