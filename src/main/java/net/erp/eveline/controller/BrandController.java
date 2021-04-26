package net.erp.eveline.controller;

import net.erp.eveline.model.ActiveBrandModel;
import net.erp.eveline.model.BrandModel;
import net.erp.eveline.service.brand.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/brand")
@CrossOrigin
public class BrandController {
    private BrandService brandService;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<BrandModel> getBrands() {
        return brandService.findAll();
    }

    @GetMapping(value = "/{brandId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public BrandModel getBrand(@PathVariable final String brandId){
        return brandService.getBrandModel(brandId);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public BrandModel upsertBrand(@RequestBody final BrandModel brandModel) {
        return brandService.upsertBrandModel(brandModel);
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ActiveBrandModel activateBrand(@RequestBody final ActiveBrandModel activeBrandModel) {
        return brandService.activateBrand(activeBrandModel);
    }

    @PutMapping(value = "/activateSet", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<ActiveBrandModel> activateBrand(@RequestBody final Set<ActiveBrandModel> activeBrandModelSet) {
        return brandService.activateBrandSet(activeBrandModelSet);
    }

    @Autowired
    public void setBrandService(BrandService brandService) {
        this.brandService = brandService;
    }
}
