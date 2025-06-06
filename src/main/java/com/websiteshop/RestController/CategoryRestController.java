package com.websiteshop.RestController;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.Sort;

import com.websiteshop.entity.Category;
import com.websiteshop.service.CategoryService;

@RestController
@CrossOrigin("*")
@RequestMapping("/rest/categories")
public class CategoryRestController {

	@Autowired
	private CategoryService categoryService;

	// Lấy tất cả danh mục
	@GetMapping()
	public List<Category> getAll() {
		return categoryService.findAll();
	}

	// Lấy một danh mục theo ID
	@GetMapping("/{id}")
	public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long categoryId) {
		Optional<Category> category = categoryService.findById(categoryId);
		return category.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Thêm mới danh mục// đã test
	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestBody Category category) {
		Category savedCategory = categoryService.save(category); // Giả sử save() lưu vào DB
		return ResponseEntity.ok(savedCategory);
	}

	// Cập nhật danh mục// đã test
	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable("id") Long categoryId,
			@RequestBody Category category) {
		Optional<Category> existingCategory = categoryService.findById(categoryId);

		if (existingCategory.isPresent()) {
			category.setCategoryId(categoryId); // Đảm bảo ID không bị thay đổi
			Category updatedCategory = categoryService.save(category);
			return ResponseEntity.ok(updatedCategory);
		}

		return ResponseEntity.notFound().build();
	}

	// Xóa loại hàng // Đã test
	@DeleteMapping("delete/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {
		try {
			// Kiểm tra xem loại hàng có tồn tại không
			if (!categoryService.existsById(categoryId)) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loại hàng không tồn tại");
			}

			// Gọi phương thức xóa
			categoryService.deleteById(categoryId);
			return ResponseEntity.ok("Loại hàng đã được xóa");
		} catch (EntityNotFoundException e) {
			// Trường hợp không tìm thấy danh mục
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Loại hàng không tồn tại");
		} catch (Exception e) {
			// Xử lý các lỗi khác
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Không thể xóa loại hàng này. Lỗi: " + e.getMessage());
		}
	}

}
