package net.erp.eveline.controller;

import net.erp.eveline.model.ProviderModel;
import net.erp.eveline.service.provider.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/provider")
public class ProviderController {

    private ProviderService providerService;

    @GetMapping(value = "/{providerId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProviderModel getProvider(@PathVariable final String providerId) {
        return providerService.getProviderModel(providerId);
    }


    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ProviderModel> getProviders() {
        return providerService.getProviderModels();
    }

    @Autowired
    public void setProviderService(final ProviderService providerService) {
        this.providerService = providerService;
    }
}
