package net.erp.eveline.controller;

import net.erp.eveline.model.ActivateProductModel;
import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProductModel;
import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
    private ProductService productService;

    @GetMapping(value = "/{providerId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<ProductModel> getProductsByProvider(@PathVariable final String providerId) {
        return productService.findAllByProvider(providerId);
    }

    @GetMapping(value = "/{productId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel getProduct(@PathVariable final String productId) {
        return productService.getProductModel(productId);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel upsertProduct(@RequestBody final ProductModel productModel) {
        return productService.upsertProductModel(productModel);
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProductModel activateProvider(@RequestBody final ActivateProductModel activeProviderModel) {
        return productService.activateProvider(activeProviderModel);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
