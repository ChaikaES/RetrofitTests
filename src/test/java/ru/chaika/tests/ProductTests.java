package ru.chaika.tests;

import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.chaika.db.model.Products;
import ru.chaika.dto.Product;
import ru.chaika.enums.CategoryType;
import ru.chaika.service.CategoryService;
import ru.chaika.service.ProductService;
import ru.chaika.utils.DbUtils;
import ru.chaika.utils.RetrofitUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProductTests {
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    Long newProductId;

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

    @AfterEach
    void tearDown() {
        if (!Objects.isNull(newProductId)) {
            DbUtils.deleteProductById(newProductId);
            newProductId = null;
        }
    }

    @Test
    void postProductTest() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();

        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));

        Products productFromDb = DbUtils.selectProductById(response.body().getId());
        assertThat(productFromDb.getTitle(), equalTo(product.getTitle()));
        assertThat(productFromDb.getPrice(), equalTo(product.getPrice()));
        assertThat(productFromDb.getCategory_id(), equalTo(new Long(CategoryType.FOOD.getId())));
    }

    @Test
    void postProductWithExistingIdTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        product.setId(newProductId);

        Response<Product> response = productService.createProduct(product).execute();

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
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        Response<Product> response2 = productService.getProduct(newProductId).execute();

        assertThat(response2.isSuccessful(), CoreMatchers.is(true));
        assertThat(response2.body().getId(), equalTo(newProductId));
        assertThat(response2.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response2.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response2.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
    }

    @Test
    void getProductByWrongIdTest() throws IOException {
        Response<Product> response = productService.getProduct(-1L).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }

    @Test
    void updateProductTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        product.setId(newProductId);
        product.setTitle("Phone");
        product.setPrice(433);
        product.setCategoryTitle(CategoryType.ELECTRONIC.getTitle());

        Response<ResponseBody> response = productService.putProduct(product).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        Products productFromDb = DbUtils.selectProductById(newProductId);
        assertThat(productFromDb.getTitle(), equalTo(product.getTitle()));
        assertThat(productFromDb.getPrice(), equalTo(product.getPrice()));
        assertThat(productFromDb.getCategory_id(), equalTo(new Long(CategoryType.ELECTRONIC.getId())));
    }

    @Test
    void updateProductWithIncorrectCategoryTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        product.setId(newProductId);
        product.setCategoryTitle("Wrong Category");

        Response<ResponseBody> response = productService.putProduct(product).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(400));
    }

    @Test
    void  updateProductParticallyTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        Product product1 = new Product().withId(newProductId);

        Response<ResponseBody> response = productService.putProduct(product1).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(400));
    }

    @Test
    void deleteProductTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        Response<ResponseBody> response = productService.deleteProduct(newProductId).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        Products productFromDb = DbUtils.selectProductById(newProductId);
        assertThat(productFromDb, CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    void deleteNonExistingProductTest() throws IOException {
        newProductId = DbUtils.createNewProduct(product.getTitle(), CategoryType.FOOD.getId(), product.getPrice());

        Response<ResponseBody> response2 = productService.deleteProduct(newProductId).execute();

        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        Response<ResponseBody> response3 = productService.deleteProduct(newProductId).execute();

        assertThat(response3.isSuccessful(), CoreMatchers.is(false));
        assertThat(response3.code(), equalTo(404));
    }

    @Test
    void deleteProductWithWrongIdTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(-1L).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(false));
        assertThat(response.code(), equalTo(404));
    }
}
