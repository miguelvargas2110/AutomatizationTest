Feature: Operaciones Password de Usuarios
  Como usuario de la API
  Quiero gestionar usuarios (crear, leer, actualizar, eliminar) And realizar autenticación
  Para poder mantener los datos de los usuarios And asegurar el acceso

  Scenario: Generación de token para cambio de contraseña
    Given Un usuario existente
    When se envía una solicitud al servicio de generacion de token para cambio de contraseña con el usuario
    Then el estado de la respuesta debe ser 200
    And se debe devolver un token JWT
    And la respuesta debe cumplir con el esquema JSON "SuccesfulJWTGeneration.json"


  Scenario: Fallo al general el token para cambio de contraseña
    Given Un usuario no existente
    When se envía una solicitud al servicio de generacion de token para cambio de contraseña con el usuario
    Then el estado de la respuesta debe ser 404
    And el mensaje de respuesta debe ser "Usuario no encontrado"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"


  Scenario: Cambio de contraseña exitoso
    Given Un usuario valido para hacer el cambio de contraseña
    When se envía una solicitud de cambio de contraseña con una nueva contraseña
    Then el estado de la respuesta debe ser 200
    And el mensaje de respuesta debe ser "Contraseña actualizada"
    And la respuesta debe cumplir con el esquema JSON "SuccesfulOperation.json"


  Scenario: Fallo al cambiar la contraseña
    Given Un usuario invalido para hacer el cambio de contraseña
    When se envía una solicitud de cambio de contraseña con una nueva contraseña
    Then el estado de la respuesta debe ser 401
    And el mensaje de respuesta debe ser "Error: Usuario no autorizado"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"
