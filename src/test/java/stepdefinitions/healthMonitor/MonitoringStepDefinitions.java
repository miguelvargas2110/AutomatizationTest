package stepdefinitions.healthMonitor;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.springframework.transaction.annotation.Transactional;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
public class MonitoringStepDefinitions {

    MonitoringDatabaseUtil monitoringDatabaseUtil = new MonitoringDatabaseUtil();
    private String baseUrlHealth = "http://localhost:8003/health";
    private Response response;
    private String serviceName;
    private String endpoint;
    private int frequency;
    private String[] notificationEmails;
    private boolean healthy;
    private String lastChecked;

    // Registro de servicio exitoso
    @Given("un nuevo servicio con detalles válidos")
    public void unNuevoServicioConDetallesValidos() {
        Faker faker = new Faker();
        serviceName = "User Service";
        endpoint = "http://localhost:8081/health";
        frequency = 60; // Cada 60 segundos
        notificationEmails = new String[] {
                "migueto2003@gmail.com"
        };
        healthy = false;
        lastChecked = "";
    }

    @When("se envía una solicitud para registrar el servicio con los datos del servicio")
    public void seEnvíaUnaSolicitudParaRegistrarElServicioConLosDatosDelServicio() {
        verificarExisteName(serviceName);
        String body = String.format(
                "{ \"name\": \"%s\", \"endpoint\": \"%s\", \"frequency\": %d, \"notificationEmails\": [\"%s\"], \"healthy\": %b, \"lastChecked\": \"%s\" }",
                serviceName,
                endpoint,
                frequency,
                notificationEmails[0],
                healthy,
                lastChecked
        );

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlHealth + "/register");
    }


    @Then("el estado de la respuesta de health debe ser {int}")
    public void elEstadoDeLaRespuestaDebeSer(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("el mensaje de respuesta de health debe ser {string}")
    public void elMensajeDeRespuestaDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("la respuesta de health debe cumplir con el esquema JSON {string}")
    public void laRespuestaDeHealthDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // Registro de servicio inválido
    @Given("un nuevo servicio con detalles inválidos")
    public void unNuevoServicioConDetallesInvalidos() {
        serviceName = "";  // Campos inválidos
        endpoint = "";
        frequency = -1; // Frecuencia inválida
        notificationEmails = new String[] {}; // Arreglo vacío
        healthy = false;
        lastChecked = "";
    }

    @When("se envía una solicitud para registrar el servicio con datos inválidos")
    public void seEnvíaUnaSolicitudParaRegistrarElServicioConDatosInválidos() {
        String body = String.format(
                "{ \"name\": \"%s\", \"endpoint\": \"%s\", \"frequency\": %d, \"notificationEmails\": %s, \"healthy\": %b, \"lastChecked\": \"%s\" }",
                serviceName,
                endpoint,
                frequency,
                "[]", // Enviar un arreglo vacío
                healthy,
                lastChecked
        );

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlHealth + "/register");
    }

    @And("el mensaje de error de respuesta de health debe ser {string}")
    public void elMensajeDeErrorDeRespuestaDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    // Consulta de todos los servicios monitoreados
    @Given("existen servicios registrados en el sistema")
    public void existenServiciosRegistradosEnElSistema() {
        // Simular el registro de un servicio
        unNuevoServicioConDetallesValidos();
        seEnvíaUnaSolicitudParaRegistrarElServicioConLosDatosDelServicio();
    }

    @When("se envía una solicitud para obtener el estado de salud de todos los servicios")
    public void seEnvíaUnaSolicitudParaObtenerElEstadoDeSaludDeTodosLosServicios() {
        response = given().get(baseUrlHealth);
    }

    @Then("el estado de la respuesta debe de health ser {int}")
    public void elEstadoDeLaRespuestaSer(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("la respuesta debe contener una lista de servicios")
    public void laRespuestaDebeContenerUnaListaDeServicios() {
        String servicios = response.jsonPath().getString("data");
        assertNotNull(servicios);
    }

    // Consulta de servicios sin registros
    @Given("no existen servicios registrados")
    public void noExistenServiciosRegistrados() {
        eliminarTodosLosServicios();
        // Implementar lógica para asegurarse de que no hay servicios registrados
        // Puede incluir eliminar registros de la base de datos si es necesario
    }

    // Consulta de un servicio específico exitoso
    @Given("existe un servicio registrado con nombre {string}")
    public void existeUnServicioRegistradoConNombre(String name) {
        serviceName = name;

        // Registrar un servicio para la prueba
        String body = String.format(
                "{ \"name\": \"%s\", \"endpoint\": \"%s\", \"frequency\": %d, \"notificationEmails\": %s, \"healthy\": %b, \"lastChecked\": \"%s\" }",
                serviceName,
                "http://localhost:8080", // Endpoint de ejemplo
                5,
                "[\"migueto2003@gmail.com\"]", // JSON array
                false,
                ""
        );

        given()
                .contentType("application/json")
                .body(body)
                .post(baseUrlHealth + "/register");
    }

    @When("se envía una solicitud para obtener el estado de salud del servicio {string}")
    public void seEnvíaUnaSolicitudParaObtenerElEstadoDeSaludDelServicio(String name) {
        response = given().get(baseUrlHealth + "/" + name);
    }

    // Consulta de un servicio específico no existente
    @Given("no existe un servicio registrado con nombre {string}")
    public void noExisteUnServicioRegistradoConNombre(String name) {
        serviceName = name;
        // Asegúrate de que no haya registros para este nombre
    }

    public boolean verificarExisteName(String name) {
        if(monitoringDatabaseUtil.verificarNombreServicioExiste(name)) {
            eliminarServicio();
            return true;
        }
        return false;
    }

    public boolean verificarExisteEndpoint(String endpoint) {
        if(monitoringDatabaseUtil.verificarEndpointServicioExiste(endpoint)) {
            return true;
        }
        return false;
    }

    public boolean verificarExisteServicio(String name, String endpoint) {
        if(verificarExisteName(name) && verificarExisteEndpoint(endpoint)) {
            return true;
        }
        return false;
    }

    public boolean hayServicios() {
        if(monitoringDatabaseUtil.hayServicios()) {
            return true;
        }
        return false;
    }

    public void eliminarServicio() {
        response = given()
                .contentType("aplication/json")
                .delete(baseUrlHealth + "/" + serviceName);
    }

    public void eliminarTodosLosServicios() {
        response = given()
                .contentType("application/json")
                .delete(baseUrlHealth + "/all");
    }

}
