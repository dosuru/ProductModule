package com.prasad.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prasad.product.Entity.Product;
import com.prasad.product.Exceptions.NoSuchProductExistsException;
import com.prasad.product.Exceptions.SizeNotFoundException;
import com.prasad.product.Exceptions.ZeroChargeException;
import com.prasad.product.Repository.IProductRepository;
import com.prasad.product.Service.ProductServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProductTest {
    @Mock
    private IProductRepository productRepo;

    @InjectMocks
    private ProductServiceImpl impl;
	

    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setProductId(1);
        product.setName("laptop");
        product.setCharges(2000);
        product.setQuantity(2);
        product.setCategory("Home Appliances");
        product.setSize("small");
    }

    @Test
    public void testAddProduct() {
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product savedProduct = impl.addProduct(product);

        assertNotNull(savedProduct);
        assertEquals(product, savedProduct);
        verify(productRepo, times(1)).save(product);
    }

    @Test
    public void testUpdateProduct_WhenProductExists() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.of(product));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = impl.updateProduct(1, product);

        assertNotNull(updatedProduct);
        assertEquals(product, updatedProduct);
        verify(productRepo, times(1)).findById(1);
        verify(productRepo, times(1)).save(product);
    }

    @Test
    public void testUpdateProduct_WhenProductDoesNotExist() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchProductExistsException.class, () -> impl.updateProduct(1, product));

        verify(productRepo, times(1)).findById(1);
        verify(productRepo, times(0)).save(any(Product.class));
    }

    @Test
    public void testDeleteProduct_WhenProductExists() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.of(product));

        String result = impl.deleteProduct(1);

        assertEquals("Product deleted successfully", result);
        verify(productRepo, times(1)).findById(1);
        verify(productRepo, times(1)).delete(product);
    }

    @Test
    public void testDeleteProduct_WhenProductDoesNotExist() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchProductExistsException.class, () -> impl.deleteProduct(1));

        verify(productRepo, times(1)).findById(1);
        verify(productRepo, times(0)).delete(any(Product.class));
    }

    @Test
    public void testGetProduct_WhenProductExists() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.of(product));

        Product retrievedProduct = impl.getProduct(1);

        assertNotNull(retrievedProduct);
        assertEquals(product, retrievedProduct);
        verify(productRepo, times(1)).findById(1);
        
    }

    @Test
    public void testGetProduct_WhenProductDoesNotExist() {
        when(productRepo.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchProductExistsException.class, () -> impl.getProduct(1));

        verify(productRepo, times(1)).findById(1);
    }

    @Test
    public void testViewAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepo.findAll()).thenReturn(productList);

        List<Product> retrievedProducts = impl.viewAllProducts();

        assertNotNull(retrievedProducts);
        assertEquals(productList, retrievedProducts);
        verify(productRepo, times(1)).findAll();
    }

    @Test
    public void testViewProductsByCharges_WithNonZeroCharges() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepo.findAll()).thenReturn(productList);

        List<Product> retrievedProducts = impl.viewProductsByCharges(2000);

        assertNotNull(retrievedProducts);
        assertEquals(productList, retrievedProducts);
        verify(productRepo, times(1)).findAll();
    }

    @Test
    public void testViewProductsByCharges_WithZeroCharges() {
        assertThrows(ZeroChargeException.class, () -> impl.viewProductsByCharges(0));

        verify(productRepo, times(0)).findAll();
    }

    @Test
    public void testViewProductsBySize_WithValidSize() throws SizeNotFoundException {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepo.findAll()).thenReturn(productList);

        List<Product> retrievedProducts = impl.viewProductsBySize("small");

        assertNotNull(retrievedProducts);
        assertEquals(productList, retrievedProducts);
        verify(productRepo, times(1)).findAll();
    }

    @Test
    public void testViewProductsBySize_WithInvalidSize() {
        assertThrows(SizeNotFoundException.class, () -> impl.viewProductsBySize(null));

        verify(productRepo, times(0)).findAll();
    }
}
