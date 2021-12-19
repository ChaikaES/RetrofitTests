package ru.chaika.tests;

import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import ru.chaika.db.dao.CategoriesMapper;
import ru.chaika.db.model.Categories;
import ru.chaika.dto.Category;
import ru.chaika.enums.CategoryType;
import ru.chaika.service.CategoryService;
import ru.chaika.utils.DbUtils;
import ru.chaika.utils.RetrofitUtils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class GetCategoryTest {
    static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @SneakyThrows
    @Test
    void getCategoryByIdPositiveTest() {
        Integer newCategoryId = DbUtils.createNewCategory("Auto");

        Response<Category> response = categoryService.getCategory(newCategoryId).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(newCategoryId));
        assertThat(response.body().getTitle(), equalTo("Auto"));

        response.body().getProducts().forEach(product ->
                assertThat(product.getCategoryTitle(), equalTo("Auto")));
    }

    @SneakyThrows
    @Test
    void getCategoryNotFoundTest() {
        Response<Category> response = categoryService.getCategory(989).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }
}
