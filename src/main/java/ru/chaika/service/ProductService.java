package ru.chaika.service;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.chaika.dto.Product;

import java.util.ArrayList;

public interface ProductService {
    @GET("products")
    Call<ArrayList<Product>> getProducts();

    @GET("products/{id}")
    Call<Product> getProduct(@Path("id") Long id);

    @POST("products")
    Call<Product> createProduct(@Body Product product);

    @PUT("products")
    Call<ResponseBody> putProduct(@Body Product product);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") Long id);
}
