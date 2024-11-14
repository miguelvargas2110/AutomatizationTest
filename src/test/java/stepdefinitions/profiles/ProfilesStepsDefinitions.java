package stepdefinitions.profiles;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProfilesStepsDefinitions {

    private Response response;
    private String perfilId;
    private String perfilData;
    private String baseUrl = "http://localhost:8000";  // Cambiar según el entorno

    // Escenarios de Creación de Perfil
    @Given("un perfil con detalles válidos y un campo \"id_usuario\" proporcionado")
    public void unPerfilConDetallesValidos() {
        perfilData = "{ \"id_usuario\": \"123\", \"nombre\": \"Juan Perez\", \"email\": \"juan@example.com\" }";
    }

    @When("se envía una solicitud POST a la API Gateway para crear un perfil con los datos del perfil")
    public void seEnvíaUnaSolicitudPOSTParaCrearPerfil() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .post(baseUrl + "/profiles/");
    }

    @Then("el estado de la respuesta debe ser {int}")
    public void elEstadoDeLaRespuestaDebeSer(int statusCode) {
        assertEquals(statusCode, response.getStatusCode());
    }

    @And("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("la respuesta debe cumplir con el esquema JSON {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // Escenarios de Creación con Error (falta id_usuario)
    @Given("un perfil sin el campo \"id_usuario\" proporcionado")
    public void unPerfilSinElCampoIdUsuario() {
        perfilData = "{ \"nombre\": \"Juan Perez\", \"email\": \"juan@example.com\" }";  // Sin el campo "id_usuario"
    }

    @When("se envía una solicitud POST a la API Gateway para crear un perfil sin el campo requerido")
    public void seEnvíaUnaSolicitudPOSTParaCrearPerfilConError() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .post(baseUrl + "/profiles/");
    }

    @And("el mensaje de error de respuesta debe ser {string}")
    public void elMensajeDeErrorDeRespuestaDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    // Escenarios de obtención de perfiles
    @Given("existen perfiles registrados en el servicio de perfiles")
    public void existenPerfilesRegistrados() {
        // Aquí podríamos simular que existen perfiles, o directamente asegurarnos de que el perfil está creado
        perfilData = "{ \"id_usuario\": \"123\", \"nombre\": \"Juan Perez\", \"email\": \"juan@example.com\" }";
        given().contentType("application/json").body(perfilData).post(baseUrl + "/profiles/");
    }

    @When("se envía una solicitud GET a la API Gateway para obtener todos los perfiles")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfiles() {
        response = given().get(baseUrl + "/profiles/");
    }

    @Then("la respuesta debe contener una lista de perfiles")
    public void laRespuestaDebeContenerUnaListaDePerfiles() {
        String perfiles = response.jsonPath().getString("data"); // "data" contiene los perfiles según ApiSuccess
        assertNotNull(perfiles);
    }

    @And("la respuesta debe cumplir con el esquema JSON {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSONParaLista(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    // Escenarios de obtención de perfil específico
    @Given("existe un perfil registrado con \"id_usuario\" \"123\"")
    public void existeUnPerfilRegistrado() {
        perfilId = "123"; // Simulamos que ya existe
    }

    @When("se envía una solicitud GET a la API Gateway para obtener el perfil con \"id_usuario\" \"123\"")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfilEspecifico() {
        response = given().get(baseUrl + "/profiles/" + perfilId + "/");
    }

    @Then("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaPerfilDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    // Escenarios de actualización de perfil
    @Given("un perfil con \"id_usuario\" \"123\" existente")
    public void unPerfilExistente() {
        perfilId = "123";
        perfilData = "{ \"nombre\": \"Juan Perez Actualizado\", \"email\": \"juan_actualizado@example.com\" }";
    }

    @When("se envía una solicitud PUT a la API Gateway para actualizar el perfil con \"id_usuario\" \"123\" con nuevos datos")
    public void seEnvíaUnaSolicitudPUTParaActualizarPerfil() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .put(baseUrl + "/profiles/" + perfilId + "/");
    }

    @Then("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaActualizarPerfilDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    // Escenarios de eliminación de perfil
    @Given("un perfil con \"id_usuario\" \"123\" existente")
    public void perfilExistenteParaEliminacion() {
        perfilId = "123"; // El perfil existe
    }

    @When("se envía una solicitud DELETE a la API Gateway para eliminar el perfil con \"id_usuario\" \"123\"")
    public void seEnvíaUnaSolicitudDELETEParaEliminarPerfil() {
        response = given().delete(baseUrl + "/profiles/" + perfilId + "/");
    }

    @Then("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaEliminarPerfilDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    // Escenarios de errores generales
    @Given("el servicio de perfiles no está disponible")
    public void servicioNoDisponible() {
        // Aquí podríamos simular que el servicio está caído o deshabilitado
    }

    @When("se envía una solicitud a la API Gateway para realizar una operación sobre los perfiles")
    public void seEnvíaUnaSolicitudDeErrorInterno() {
        response = given().get(baseUrl + "/profiles/");
    }

    @Then("el estado de la respuesta debe ser 500")
    public void elEstadoDeLaRespuestaDeErrorInterno() {
        assertEquals(500, response.getStatusCode());
    }

    @And("la respuesta debe cumplir con el esquema JSON {string}")
    public void laRespuestaDeErrorDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }
}
