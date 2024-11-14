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
    private String baseUrl = "http://localhost:8000/api";
    private ProfilesDatabaseUtil profilesDatabaseUtil = new ProfilesDatabaseUtil();

    // Escenarios de Creación de Perfil
    @Given("un perfil valido")
    public void unPerfilConDetallesValidos() {
        verificarPerfilNoE();
        perfilData = "{ \"id_usuario\": \"123\", \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";
    }

    // Escenarios de Creación con Error (falta id_usuario)
    @Given("un perfil invalido")
    public void unPerfilSinElCampoIdUsuario() {
        perfilData = "{ \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";  // Sin el campo "id_usuario"
    }

    @When("se envía una solicitud POST para crear un perfil")
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

    @And("el mensaje de error de respuesta debe ser {string}")
    public void elMensajeDeErrorDeRespuestaDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    // Escenarios de obtención de perfiles
    @Given("existen perfiles registrados en el servicio de perfiles")
    public void existenPerfilesRegistrados() {
        verificarPerfilE();
    }

    @Given("no existen perfiles registrados en el servicio de perfiles")
    public void noExistenPerfilesRegistradosEnElServicioDePerfiles() {
        profilesDatabaseUtil.borrarTodosLosPerfiles();
    }

    @When("se envía una solicitud GET para obtener todos los perfiles")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfiles() {
        response = given().get(baseUrl + "/profiles/");
    }

    @Then("la respuesta debe contener una lista de perfiles")
    public void laRespuestaDebeContenerUnaListaDePerfiles() {
        String perfiles = response.jsonPath().getString("data"); // "data" contiene los perfiles según ApiSuccess
        assertNotNull(perfiles);
    }


    @Given("un perfil inexistente")
    public void unPerfilInexistente() {
        verificarPerfilIne();
        perfilId = "999";
    }

    @When("se envía una solicitud GET para obtener el perfil")
    public void seEnvíaUnaSolicitudGETParaObtenerPerfilEspecifico() {
        response = given().get(baseUrl + "/profiles/" + perfilId + "/");
    }

    // Escenarios de actualización de perfil
    @Given("un perfil existente")
    public void unPerfilExistente() {
        verificarPerfilE();
        perfilId = "123";
    }

    @And("los datos de perfil son válidos")
    public void losDatosDePerfilSonVálidos() {
        perfilData = "{ \"informacion_publica\": \"true\", \"apodo\": \"Juancho\" }";
    }

    @And("los datos de perfil son inválidos")
    public void losDatosDePerfilSonInválidos() {
        perfilData = "{ \"informacion_publica\": \"Si\", \"apodo\": \"Juancho\" }";
    }

    @When("se envía una solicitud PUT para actualizar el perfil")
    public void seEnvíaUnaSolicitudPUTParaActualizarPerfil() {
        response = given()
                .contentType("application/json")
                .body(perfilData)
                .put(baseUrl + "/profiles/" + perfilId + "/");
    }

    @When("se envía una solicitud DELETE para eliminar el perfil")
    public void seEnvíaUnaSolicitudDELETEParaEliminarPerfil() {
        response = given().delete(baseUrl + "/profiles/" + perfilId + "/");
    }

    @Then("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaEliminarPerfilDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("la respuesta debe cumplir con el esquema JSON {string}")
    public void laRespuestaDeErrorDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
    }

    public void verificarPerfilIne(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(999);
        if(existe){
            given().delete(baseUrl + "/profiles/999/");
        }
    }

    public void verificarPerfilNoE(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(123);
        if(existe){
            given().delete(baseUrl + "/profiles/123/");
        }
    }

    public void verificarPerfilE(){
        boolean existe = profilesDatabaseUtil.verificarPerfilExistente(123);
        if(!existe){
            perfilData = "{ \"id_usuario\": \"123\", \"informacion_publica\": \"false\", \"apodo\": \"Juan\" }";
            given().contentType("application/json").body(perfilData).post(baseUrl + "/profiles/");
        }
    }
}
