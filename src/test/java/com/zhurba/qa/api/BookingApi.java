package com.zhurba.qa.api;

import com.zhurba.qa.api.model.Booking;
import com.zhurba.qa.config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.HttpClientConfig.httpClientConfig;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

public class BookingApi {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(Config.API_BASE_URL)
            .setContentType(JSON)
            // Booker matches Accept exactly; ContentType.JSON expands to several media types.
            .setAccept("application/json")
            .setConfig(config()
                    .httpClient(httpClientConfig()
                            .setParam("http.connection.timeout", Config.API_TIMEOUT)
                            .setParam("http.socket.timeout", Config.API_TIMEOUT))
                    .logConfig(logConfig()
                            .enableLoggingOfRequestAndResponseIfValidationFails()
                            .blacklistHeader("Cookie", "Authorization")))
            .build();

    public Response createBooking(Object body) {
        return request().body(body).post("/booking");
    }

    public Response getBooking(int bookingId) {
        return request().get("/booking/{id}", bookingId);
    }

    public Response updateBooking(int bookingId, String token, Booking booking) {
        return request()
                .cookie("token", token)
                .body(booking)
                .put("/booking/{id}", bookingId);
    }

    public Response deleteBooking(int bookingId, String token) {
        return request().cookie("token", token).delete("/booking/{id}", bookingId);
    }

    public String getToken() {
        return request()
                .body(Map.of("username", Config.API_USERNAME, "password", Config.API_PASSWORD))
                .post("/auth")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .body("token", not(emptyOrNullString()))
                .extract().path("token");
    }

    private RequestSpecification request() {
        return given().spec(spec);
    }
}
