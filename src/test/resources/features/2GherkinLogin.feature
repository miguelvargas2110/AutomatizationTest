Feature: Operaciones Login de Usuarios
  Como usuario de la API
  Quiero gestionar usuarios (crear, leer, actualizar, eliminar) And realizar autenticación
  Para poder mantener los datos de los usuarios And asegurar el acceso


  Scenario: Inicio de sesión exitoso del usuario
    Given un usuario con credenciales válidas
    When se envía una solicitud de autenticacion con esas credenciales
    Then el estado de la respuesta debe ser 200
    And se debe devolver un token JWT
    And la respuesta debe cumplir con el esquema JSON "SuccesfulJWTGeneration.json"

  Scenario: Inicio de sesión fallido del usuario con credenciales inválidas
    Given un usuario con credenciales inválidas
    When se envía una solicitud de autenticacion con esas credenciales
    Then el estado de la respuesta debe ser 401
    And el mensaje de error debe ser "Usuario o contraseña no validos"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

