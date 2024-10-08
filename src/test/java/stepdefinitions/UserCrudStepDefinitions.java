package stepdefinitions;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UserCrudStepDefinitions {

    UserDatabaseUtil userDatabaseUtil = new UserDatabaseUtil();
    private String baseUrl = "http://localhost:8001";
    private Response response;
    private String username = "";
    private String password = "";
    private String email = "";
    private String token = "";
    private int tamanoPagina;
    private int numeroPagina;

    //SignUp
    @Given("un nuevo usuario con detalles válidos")
    public void unNuevoUsuarioConDetallesVálidos() {
        Faker faker = new Faker();

        // Generar datos aleatorios
        username = faker.name().firstName();
        password = faker.internet().password(8, 16);
        email = faker.internet().emailAddress();

        System.out.println("Generado usuario: " + username + ", email: " + email + ", password: " + password);
    }



    @Given("un nuevo usuario con detalles invalidos")
    public void unNuevoUsuarioConDetallesInvalidos() {
        verificarUsuarioNoE();
        username = "Ortiz";
        password = "123456";
        email = "ortiz@gmail.com";
    }

    @When("se envía una solicitud de registro con los datos de ese usuario")
    public void seEnvíaUnaSolicitudDeRegistroConLosDatosDeEseUsuario() {
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\", \"email\": \"" + email + "\"}";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/signup");
    }

    @And("la respuesta debe cumplir con el esquema JSON {string}")
    public void laRespuestaDebeCumplirConElEsquemaJSON(String schemaPath) {
        response.then().assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }

    //Login
    @Given("un usuario con credenciales válidas")
    public void unUsuarioConCredencialesValidas() {
        username = "Ortiz";
        password = "1234";
    }

    @Given("un usuario con credenciales inválidas")
    public void unUsuarioConCredencialesInvalidas() {
        username = "Ortiz";
        password = "123456";
    }

    @When("se envía una solicitud de autenticacion con esas credenciales")
    public void seEnvíaUnaSolicitudDeAutenticacionConEsasCredenciales() {
        String body = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/login");
    }

    @Then("se debe devolver un token JWT")
    public void seDebeDevolverUnTokenJWT() {
        token = response.jsonPath().getString("jwt");
        assertNotNull(token);
    }

    //Autenticacion de usuario
    @Given("Un usuario autenticado")
    public void unUsuarioAutenticado() {
        verificarUsuarioNoE();
    }

    @Given("Un usuario no autenticado")
    public void unUsuarioNoAutenticado() {
        token = "";
    }

    //Update
    @And("intenta actualizarse a si mismo")
    public void intentaActualizarseASiMismo() {
        username = "Ortiz";
        password = "5678";
        email = "ortiz@gmail.com";
    }

    @And("intenta actualizar otro usuario")
    public void intentaActualizarOtroUsuario() {
        username = "Tunubala";
        password = "5678";
        email = "ortiz@gmail.com";

    }

    @When("se envía una solicitud de actualizacion con los datos")
    public void seEnvíaUnaSolicitudDeActualizacionConLosDatos() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("username", username)
                .multiPart("password", password)
                .multiPart("email", email)
                .put(baseUrl + "/user");
    }

    @When("se envía una solicitud de actualizacion sin los datos")
    public void seEnvíaUnaSolicitudDeActualizacionSinLosDatos() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("username", username)
                .put(baseUrl + "/user");
    }

    //Delete
    @And("intenta eliminarse a si mismo")
    public void intentaEliminarseASiMismo() {
        username = "Ortiz";
    }

    @And("intenta eliminar otro usuario")
    public void intentaEliminarOtroUsuario() {
        username = "Tunubala";
    }

    @When("se envía una solicitud de eliminacion con el usuario")
    public void seEnvíaUnaSolicitudDeEliminacionConElUsuario() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("username", username)
                .delete(baseUrl + "/user");
    }

    //Pageable
    @Given("Hay usuarios en la base de datos")
    public void hayUsuariosEnLaBaseDeDatos() {

    }

    @And("se dan valores positivos para {int} y {int}")
    public void seDanValoresPositivosParaY(int tamanoPagina, int numeroPagina) {
        this.tamanoPagina = tamanoPagina;
        this.numeroPagina = numeroPagina;
    }

    @And("la respuesta debe contener una lista de usuarios con detalles de paginación")
    public void laRespuestaDebeContenerUnaListaDeUsuariosConDetallesDePaginación() {
        String mensaje = response.jsonPath().getString("message");
    }

    @And("se dan valores negativos para {int} y {int}")
    public void seDanValoresNegativosParaY(int tamanoPagina, int numeroPagina) {
        this.tamanoPagina = tamanoPagina;
        this.numeroPagina = numeroPagina;
    }

    @When("se envía una solicitud de paginacion con {int} y {int}")
    public void seEnvíaUnaSolicitudDePaginacionConY(int tamanoPagina, int numeroPagina) {
        response = given()
                .contentType("multipart/form-data")
                .multiPart("numeroPagina", numeroPagina)
                .multiPart("tamanoPagina", tamanoPagina)
                .get(baseUrl + "/users");
    }

    //Global
    @Then("el estado de la respuesta debe ser {int}")
    public void elEstadoDeLaRespuestaDebeSer(Integer statusCode) {
        assertEquals(statusCode.intValue(), response.getStatusCode());
    }

    @And("el mensaje de respuesta debe ser {string}")
    public void elMensajeDeRespuestaDebeSer(String message) {
        String mensaje = response.jsonPath().getString("message");
        assertEquals(message, mensaje);
    }

    @And("el mensaje de error debe ser {string}")
    public void elMensajeDeErrorDebeSer(String errorMessage) {
        String mensajeError = response.jsonPath().getString("message");
        assertEquals(errorMessage, mensajeError);
    }

    //generateChangePasswordToken
    @Given("Un usuario existente")
    public void unUsuarioExistente() {
        username = "Ortiz";
    }

    @Given("Un usuario no existente")
    public void unUsuarioNoExistente() {
        username = "Miguel";
    }

    @When("se envía una solicitud al servicio de generacion de token para cambio de contraseña con el usuario")
    public void seEnvíaUnaSolicitudAlServicioDeGeneracionDeTokenParaCambioDeContraseñaConElUsuario() {
        response = given()
                .contentType("multipart/form-data")
                .multiPart("username", username)
                .post(baseUrl + "/generateChangePasswordToken");
    }

    //ChangePassword
    @Given("Un usuario valido para hacer el cambio de contraseña")
    public void unUsuarioValidoParaHacerElCambioDeContraseña() {
        username = "Ortiz";
        password = "1234566";
        generarTokenCambio();
    }


    @Given("Un usuario invalido para hacer el cambio de contraseña")
    public void unUsuarioInvalidoParaHacerElCambioDeContraseña() {
        username = "Tunubala";
        password = "1234";
        generarTokenCambio();
    }

    @When("se envía una solicitud de cambio de contraseña con una nueva contraseña")
    public void seEnvíaUnaSolicitudDeCambioDeContraseñaConUnaNuevaContraseña() {
        response = given()
                .header("Authorization", "Bearer " + token)
                .contentType("multipart/form-data")
                .multiPart("username", username)
                .multiPart("password", password)
                .post(baseUrl + "/changePassword");
    }

    public void crearUsuario(){
        username = "Ortiz";
        password = "1234";
        email = "ortiz@gmail.com";
        seEnvíaUnaSolicitudDeRegistroConLosDatosDeEseUsuario();
    }

    public void borrarUsuario(){
        username = "Ortiz";
        seEnvíaUnaSolicitudDeEliminacionConElUsuario();
    }

    public void verificarUsuarioNoE(){
        boolean existe = userDatabaseUtil.verificarUsuarioExiste("Ortiz");
        if(!existe){
            crearUsuario();
        }else{
            restablecerContraseña("1234");
        }
        generarToken();
    }

    public void verificarUsuarioE(){
        boolean existe = userDatabaseUtil.verificarUsuarioExiste("Ortiz");
        if(existe){
            generarToken();
            borrarUsuario();
        }
    }

    public void generarToken(){

        String body = "{ \"username\": \"Ortiz\", \"password\": \"1234\" }";

        response = given()
                .contentType("application/json")
                .body(body)
                .post(baseUrl + "/login");

        token = response.jsonPath().getString("jwt");
    }

    public void generarTokenCambio(){
        response = given()
                .contentType("multipart/form-data")
                .multiPart("username", "Ortiz")
                .post(baseUrl + "/generateChangePasswordToken");
        token = response.jsonPath().getString("jwt");
    }

    public void restablecerContraseña(String newPassword) {
        // Aquí realizas la solicitud para cambiar la contraseña sin autenticación
        response = given()
                .contentType("multipart/form-data")
                .multiPart("password", newPassword)
                .post(baseUrl + "/resetPassword");  // Cambia a la ruta que maneje restablecimiento de contraseñas
    }
}