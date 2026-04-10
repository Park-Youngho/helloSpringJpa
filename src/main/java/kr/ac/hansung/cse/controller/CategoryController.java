package kr.ac.hansung.cse.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.cse.exception.DuplicateCategoryException;
import kr.ac.hansung.cse.model.Category;
import kr.ac.hansung.cse.model.CategoryForm;
import kr.ac.hansung.cse.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /categories - 카테고리 목록 조회
    // ─────────────────────────────────────────────────────────────────
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categoryList"; // categoryList.html 반환
    }

    // ─────────────────────────────────────────────────────────────────
    // GET /categories/create - 카테고리 등록 폼 표시
    // ─────────────────────────────────────────────────────────────────
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryForm());
        return "categoryForm"; // categoryForm.html 반환
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /categories/create - 카테고리 등록 처리
    // ─────────────────────────────────────────────────────────────────
    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute CategoryForm categoryForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()) return "categoryForm"; //검증실패
        try{
            categoryService.createCategory(categoryForm.getName());
            redirectAttributes.addFlashAttribute("successMessage", "등록완료");
        }catch (DuplicateCategoryException e){
            // 중복 예외 -> BindingResult에 필드 오류 등록 후 폼 재표시
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
            return "categoryForm";
        }
        return "redirect:/categories";
    }
}
