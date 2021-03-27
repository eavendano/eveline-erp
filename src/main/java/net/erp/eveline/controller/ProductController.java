package net.erp.eveline.controller;

import net.erp.eveline.model.ActiveProductModel;
import net.erp.eveline.model.product.ProductModel;
import net.erp.eveline.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    private ProductService productService;

    @GetMapping(value = "/provider/{providerId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<ProductModel> getProductsByProvider(@PathVariable final String providerId) {
        return productService.findAllByProvider(providerId);
    }

    @GetMapping(value = "/{productId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel getProduct(@PathVariable final String productId) {
        return productService.getProductModel(productId);
    }

    @GetMapping(value = "/upc/{upc}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel getProductByUpc(@PathVariable final String upc) {
        return productService.findByUpc(upc);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel upsertProduct(@RequestBody final ProductModel productModel) {
        return productService.upsertProductModel(productModel);
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ActiveProductModel activateProvider(@RequestBody final ActiveProductModel activeProviderModel) {
        return productService.activateProduct(activeProviderModel);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
