package ru.chaika.utils;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import ru.chaika.db.dao.CategoriesMapper;
import ru.chaika.db.dao.ProductsMapper;
import ru.chaika.db.model.Categories;
import ru.chaika.db.model.CategoriesExample;
import ru.chaika.db.model.Products;
import ru.chaika.db.model.ProductsExample;

import java.io.IOException;

@UtilityClass
public class DbUtils {
    private static  String resource = "mybatisConfig.xml";
    static Faker faker = new Faker();
    private static SqlSession getSqlSession() throws IOException {
        SqlSessionFactory sqlSessionFactory;
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream(resource));
        return sqlSessionFactory.openSession(true);
    }

    @SneakyThrows
    public static CategoriesMapper getCategoriesMapper(){
        return getSqlSession().getMapper(CategoriesMapper.class);
    }

    @SneakyThrows
    public static ProductsMapper getProductsMapper() {
        return getSqlSession().getMapper(ProductsMapper.class);
    }

    public static Integer createNewCategory(String title) {
        CategoriesMapper categoriesMapper = getCategoriesMapper();
        Categories newCategory = new Categories();
        newCategory.setTitle(title);

        categoriesMapper.insert(newCategory);

        return newCategory.getId();
    }

    public static Long createNewProduct(String title, Integer categoryId, Integer price) {
        ProductsMapper productsMapper = getProductsMapper();
        Products newProduct = new Products();
        newProduct.setTitle(title);
        newProduct.setPrice(price);
        newProduct.setCategory_id(new Long(categoryId));

        productsMapper.insert(newProduct);

        return newProduct.getId();
    }

    public static Products selectProductById(Long productId) {
        ProductsMapper productsMapper = getProductsMapper();

        return productsMapper.selectByPrimaryKey(productId);
    }

    public static void deleteProductById(Long productId) {
        ProductsMapper productsMapper = getProductsMapper();
        productsMapper.deleteByPrimaryKey(productId);
    }

    public static Integer countCategories() {
        CategoriesMapper categoriesMapper = getCategoriesMapper();
        long categoriesCount = categoriesMapper.countByExample(new CategoriesExample());
        return Math.toIntExact(categoriesCount);
    }

    public static Integer countProducts() {
        ProductsMapper productsMapper = getProductsMapper();
        long products = productsMapper.countByExample(new ProductsExample());
        return Math.toIntExact(products);
    }
}
