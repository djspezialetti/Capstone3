package org.yearup.controllers;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;

    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    @GetMapping()
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.notFound().build();
            //return ResponseEntity.ok(categoryDao.getAllCategories());
        }
        return ResponseEntity.ok(categoryDao.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getById(@PathVariable int id) {
        Category category = categoryDao.getById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping("/categories/{categoryId}/products")
    public ResponseEntity<List<Product>> getProductsById(@PathVariable int categoryId) {
        List<Product> productList = new ArrayList<>();
        Product product = productDao.getById(categoryId);
        if (product == null) {
            return  ResponseEntity.notFound().build();
        }
        else {
            productList.add(product);
            return ResponseEntity.ok(productList);
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {
        try {
            Category created = categoryDao.create(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable int id, @RequestBody Category category) {
        try {
            Category updated = categoryDao.update(id, category);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        boolean deleted = categoryDao.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
