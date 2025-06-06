package com.websiteshop.AdminController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.IOException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.websiteshop.entity.Category;
import com.websiteshop.model.CategoryDto;
import com.websiteshop.service.CategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryAdminController {

	@Autowired
	private RestTemplate restTemplate; // Inject RestTemplate for API calls

	@Autowired
	CategoryService categoryService;

	@Value("${api.categories.url}") // URL của API từ file application.properties
	private String categoriesApiUrl;

	// GET method to display categories with pagination
	@RequestMapping("")
	public String search(ModelMap model,
			@RequestParam(name = "name", required = false) String name,
			@RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);
		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("categoryId").descending());
		Page<Category> resultPage = null;

		if (StringUtils.hasText(name)) {
			resultPage = categoryService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
			long totalSize = categoryService.countByNameContaining(name);
			model.addAttribute("totalSize", totalSize);
		} else {
			resultPage = categoryService.findAll(pageable);
			List<Category> resultList = categoryService.findAll();
			// for( Category category : resultList ) {
			// System.out.println(category.getCategoryId());
			// System.out.println(category.getName());
			// }
			int totalSize = resultList.size();
			model.addAttribute("totalSize", totalSize);
		}

		int totalPages = resultPage.getTotalPages();
		if (totalPages > 0) {
			int start = Math.max(1, currentPage - 2);
			int end = Math.min(currentPage + 2, totalPages);

			if (totalPages > 5) {
				if (end == totalPages)
					start = end - 5;
				else if (start == 1)
					end = start + 5;
			}
			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
					.boxed()
					.collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}

		model.addAttribute("categoryPage", resultPage);
		return "admin/category/list";
	}

	// GET method to add a new category
	@GetMapping("add")
	public String add(Model model) {
		CategoryDto dto = new CategoryDto();
		dto.setIsEdit(false); // Đặt isEdit = false vì đây là thao tác thêm mới
		model.addAttribute("category", dto);
		return "admin/category/addOrEdit"; // Chuyển hướng đến trang thêm hoặc chỉnh sửa loại hàng
	}

	// POST method to save or update a category
	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(ModelMap model, @ModelAttribute("category") CategoryDto dto,
			BindingResult result) {
		Category entity = new Category();
		BeanUtils.copyProperties(dto, entity);

		// Save category via API (you can implement API call to save or update category)
		restTemplate.postForObject(categoriesApiUrl, entity, Category.class); // Modify API to support POST for creating
																				// categories

		model.addAttribute("message", "Loại hàng đã được lưu");
		// return new ModelAndView("forward:/admin/categories", model);
		return "redirect:/admin/categories";

	}

	@GetMapping("edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {

		Optional<Category> opt = categoryService.findById(categoryId);
		CategoryDto dto = new CategoryDto();

		if (opt.isPresent()) {
			Category entity = opt.get();
			BeanUtils.copyProperties(entity, dto);
			dto.setIsEdit(true);

			model.addAttribute("category", dto);
			return new ModelAndView("admin/category/addOrEdit", model);
		}

		model.addAttribute("message", "Loại hàng không tồn tại");

		return new ModelAndView("forward:/admin/categories", model);
	}

	@DeleteMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		try {
			// Gửi yêu cầu DELETE đến API
			String deleteApiUrl = categoriesApiUrl + "/delete/" + categoryId;
			System.out.println("Calling API URL: " + deleteApiUrl); // In ra URL để kiểm tra

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(deleteApiUrl, HttpMethod.DELETE, HttpEntity.EMPTY,
					String.class);

			// Kiểm tra phản hồi từ API
			if (response.getStatusCode() == HttpStatus.OK) {
				model.addAttribute("message", "Loại hàng đã được xóa");
			} else {
				model.addAttribute("message", "Xóa loại hàng thất bại. Mã lỗi: " + response.getStatusCode());
			}
		} catch (Exception e) {
			e.printStackTrace(); // In chi tiết lỗi để debug
			model.addAttribute("message", "Không thể xóa loại hàng. Lỗi: " + e.getMessage());
		}

		// Chuyển hướng về danh sách danh mục
		return new ModelAndView("forward:/admin/categories", model);
		// return "redirect:/admin/categories";
	}
}
