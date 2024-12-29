package com.example.resource;

import com.example.entity.Appointment;
import com.example.entity.User;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@Testcontainers
class AppointmentResourceIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("healthcaredb")
            .withUsername("yourusername")
            .withPassword("yourpassword");

    @Test
    void testCreateAndGetAppointment() {
        var patient = new User();
        patient.username = "patient1";
        patient.password = "pass123";
        patient.role = "PATIENT";
        patient.fullName = "Patient One";
        patient.email = "patient1@example.com";

        RestAssured.given()
                .contentType("application/json")
                .body(patient)
                .when().post("/users")
                .then()
                .statusCode(200)
                .body("username", is(patient.username));

        var doctor = new User();
        doctor.username = "doctor1";
        doctor.password = "pass456";
        doctor.role = "DOCTOR";
        doctor.fullName = "Doctor One";
        doctor.email = "doctor1@example.com";

        RestAssured.given()
                .contentType("application/json")
                .body(doctor)
                .when().post("/users")
                .then()
                .statusCode(200)
                .body("username", is("doctor1"));

        var appointment = new Appointment();
        appointment.patient = new User();
        appointment.patient.id = 1L;
        appointment.doctor = new User();
        appointment.doctor.id = 2L;
        appointment.appointmentTime = java.time.LocalDateTime.now().plusDays(1);
        appointment.status = "SCHEDULED";
        appointment.notes = "Initial Consultation";

        RestAssured.given()
                .contentType("application/json")
                .body(appointment)
                .when().post("/appointments")
                .then()
                .statusCode(200)
                .body("status", is("SCHEDULED"));

    }
}
