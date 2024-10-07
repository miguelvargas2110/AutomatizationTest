package stepdefinitions.logs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.google.common.base.Predicates.equalTo;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
public class LogsStepDefinitions {

    private String baseUrl = "http://localhost:8021";
    private Response response;
    private String logType;
    private String className;
    private String summary;
    private String description;
    private String generatedDate;
    private String token = "";

    // Creación de log exitoso
    @Given("un nuevo log con detalles válidos")
    public void unNuevoLogConDetallesValidos() {
        Faker faker = new Faker();
        logType = "INFO";
        className = faker.app().name();
        summary = faker.lorem().sentence();
        description = faker.lorem().paragraph();
        generatedDate = LocalDateTime.now().toString();
    }

    @When("se envía una solicitud de creación de log con los datos del log")
    public void seEnvíaUnaSolicitudDeCreacionDeLogConLosDatosDelLog() {
        String body = String.format(
                "{ \"application\": \"%s\", \"logType\": \"%s\", \"className\": \"%s\", \"timestamp\": \"%s\", \"summary\": \"%s\", \"description\": \"%s\" }",
                "MiAplicacion",
                logType,
                className,
                generatedDate,
                summary,
                description
        );

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/logs");
    }

    @Then("el estado de la respuesta de logs debe ser {int}")
    public void elEstadoDeLaRespuestaDebeSer(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("el mensaje de respuesta de logs debe ser {string}")
    public void elMensajeDeRespuestaDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("la respuesta de logs debe cumplir con el esquema JSON {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // Creación de log inválido
    @Given("un nuevo log con detalles inválidos")
    public void unNuevoLogConDetallesInvalidos() {
        logType = "";  // Dejar campos vacíos o inválidos
        className = "";
        summary = "";
        description = "";
        generatedDate = "";
    }

    @When("se envía una solicitud de creación de log con los datos inválidos")
    public void seEnvíaUnaSolicitudDeCreacionDeLogConLosDatosInvalidos() {
        String body = String.format(
                "{ \"logType\": \"%s\", \"className\": \"%s\", \"summary\": \"%s\", \"description\": \"%s\", \"generatedDate\": \"%s\" }",
                logType, className, summary, description, generatedDate
        );

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/logs");
    }

    @And("el mensaje de error de logs debe ser {string}")
    public void elMensajeDeErrorDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    // Consulta de logs exitosa
    @Given("existen logs registrados en el sistema")
    public void existenLogsRegistradosEnElSistema() {
        // Simular o verificar que ya hay logs en la base de datos si es necesario
        boolean existe = LogsDatabaseUtil.hayRegistros();
        if (!existe) {
            // Crear logs de prueba
            seEnvíaUnaSolicitudDeCreacionDeLogConLosDatosDelLog();
        }
    }

    @When("se envía una solicitud de consulta de logs con filtros válidos")
    public void seEnvíaUnaSolicitudDeConsultaDeLogsConFiltrosValidos() {
        response = given()
                .get(baseUrl + "/logs");
    }

    @And("la respuesta debe contener una lista de logs")
    public void laRespuestaDebeContenerUnaListaDeLogs() {
        String logs = response.jsonPath().getString("data"); // "data" contiene los logs según ApiSuccess
        assertNotNull(logs);
    }

    // Consulta de logs sin resultados
    @Given("no existen logs que cumplan con los filtros")
    public void noExistenLogsQueCumplanConLosFiltros() {
        // Simular o verificar que no hay logs que coincidan con los filtros
        boolean existe = LogsDatabaseUtil.hayRegistros();
        if (!existe) {
            eliminarTodosLosLogs();

        }
    }

    @When("se envía una solicitud de consulta de logs con filtros")
    public void seEnvíaUnaSolicitudDeConsultaDeLogsConFiltros() {
        response = given()
                .queryParam("logType", "ERROR")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .get(baseUrl + "/logs");
    }



    private void eliminarTodosLosLogs() {
        // Llamada a la API para eliminar todos los logs
        response = given()
                .contentType("application/json")
                .delete(baseUrl + "/logs/all");
    }



}
