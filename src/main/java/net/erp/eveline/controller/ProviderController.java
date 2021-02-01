package net.erp.eveline.controller;

import net.erp.eveline.model.ActiveProviderModel;
import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.provider.ProviderService;
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
@RequestMapping("/provider")
@CrossOrigin
public class ProviderController {

    private ProviderService providerService;

    @GetMapping(value = "/", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Set<ProviderModel> getProviders() {
        return providerService.findAll();
    }

    @GetMapping(value = "/{providerId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProviderModel getProvider(@PathVariable final String providerId) {
        return providerService.getProviderModel(providerId);
    }

    @PutMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProviderModel upsertProvider(@RequestBody final ProviderModel providerModel) {
        return providerService.upsertProviderModel(providerModel);
    }

    @PutMapping(value = "/activate", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProviderModel activateProvider(@RequestBody final ActiveProviderModel activeProviderModel) {
        return providerService.activateProvider(activeProviderModel);
    }

    @Autowired
    public void setProviderService(final ProviderService providerService) {
        this.providerService = providerService;
    }
}
