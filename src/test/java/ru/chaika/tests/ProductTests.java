package ru.chaika.tests;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.chaika.dto.Product;
import ru.chaika.enums.CategoryType;
import ru.chaika.dto.Category;
import ru.chaika.service.CategoryService;
import ru.chaika.service.ProductService;
import ru.chaika.utils.RetrofitUtils;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;

    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().fruit())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());
    }

    @Test
    void postProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();

        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void postProductWithExistingIdTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();

        product.setId(response.body().getId());

        response = productService.createProduct(product).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(400));
    }

    @Test
    void getListProductTest() throws IOException {
        Response<ArrayList<Product>> response = productService.getProducts().execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void getPrductByIdTest() throws IOException {
        Response<Product> response1 = productService.createProduct(product).execute();

        Response<Product> response2 = productService.getProduct(response1.body().getId()).execute();
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));
        assertThat(response2.body().getId(), equalTo(response1.body().getId()));
        assertThat(response2.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response2.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response2.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void putUpdateProductTest() throws IOException {
        Response<Product> response1 = productService.createProduct(product).execute();

        product.setId(response1.body().getId());
        product.setTitle("Phone");
        product.setPrice(433);
        product.setCategoryTitle(CategoryType.ELECTRONIC.getTitle());

        Response<ResponseBody> response2 = productService.putProduct(product).execute();

        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        Response<Product> response3 = productService.getProduct(response1.body().getId()).execute();

        assertThat(response3.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response3.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response3.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void deleteProductTest() throws IOException {
        Response<Product> response1 = productService.createProduct(product).execute();

        Response<ResponseBody> response2 = productService.deleteProduct(response1.body().getId()).execute();
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        Response<Product> response3 = productService.getProduct(response1.body().getId()).execute();
        assertThat(response3.isSuccessful(), CoreMatchers.is(false));
        assertThat(response3.code(), equalTo(404));
    }
}
