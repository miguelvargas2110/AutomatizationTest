package stepdefinitions.logs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.springframework.transaction.annotation.Transactional;
import stepdefinitions.UserDatabaseUtil;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
public class LogsStepDefinitions {

    UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil();

    private String baseUrlUsers = "http://localhost:8001";
    private String baseUrlLogs = "http://localhost:8002";

    private Response response;
    private String logType;
    private String className;
    private String summary;
    private String description;
    private String generatedDate;
    private String token = "";
    private String testUsername = "";
    private String testPassword = "1234";
    private String testEmail = "testuser@gmail.com";


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
                .post(baseUrlLogs + "/logs");
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
                .post(baseUrlLogs + "/logs");
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
                .get(baseUrlLogs + "/logs");
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
                .get(baseUrlLogs + "/logs");
    }

    private void eliminarTodosLosLogs() {
        // Llamada a la API para eliminar todos los logs
        response = given()
                .contentType("application/json")
                .delete(baseUrlLogs + "/logs/all");
    }

    //Verificación de integración
    @Given("que un usuario llamado {string} no existe en el sistema")
    public void queUnUsuarioLlamadoNoExisteEnElSistema(String username) {
        this.testUsername = username;
        verificarUsuarioELogs();
    }

    @When("el usuario {string} es creado en el sistema de autenticación")
    public void elUsuarioEsCreadoEnElSistemaDeAutenticacion(String username) {
        this.testUsername = username;

        String body = "{ \"username\": \"" + testUsername + "\", \"password\": \"" + testPassword + "\", \"email\": \"" + testEmail + "\"}";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlUsers + "/signup");
    }

    @Then("se debe registrar un log con el mensaje {string} en el sistema de logs")
    public void seDebeRegistrarUnLogConElMensajeEnElSistemaDeLogs(String description) {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("description", description)
                .get(baseUrlLogs + "/logs/logByDescription");
    }

    @And("el mensaje de la respuesta debe ser {string}")
    public void elMensajeDeLaRespuestaDebeSer(String mensaje) {
        String mensajeResponse = response.jsonPath().getString("message");
        assertEquals(mensaje, mensajeResponse);
    }

    public void verificarUsuarioELogs(){
        boolean existe = userDatabaseUtil.verificarUsuarioExiste(testUsername);
        if(existe){
            generarTokenLogs();
            borrarUsuarioLogs();
        }
    }

    public void borrarUsuarioLogs(){
        testUsername = "testUser";
        seEnvíaUnaSolicitudDeEliminacionConElUsuarioLogs();
    }

    public void seEnvíaUnaSolicitudDeEliminacionConElUsuarioLogs() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("username", testUsername)
                .delete(baseUrlUsers + "/user");
    }


    public void generarTokenLogs(){

        String body = "{ \"username\": \"Ortiz\", \"password\": \"1234\" }";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlUsers + "/login");

        token = response.jsonPath().getString("jwt");
    }






}
