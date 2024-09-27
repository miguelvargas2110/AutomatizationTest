Feature: Operaciones SignUp de Usuarios
  Como usuario de la API
  Quiero gestionar usuarios (crear, leer, actualizar, eliminar) And realizar autenticación
  Para poder mantener los datos de los usuarios And asegurar el acceso

   Scenario: Registro de usuario exitoso
     Given un nuevo usuario con detalles válidos
     When se envía una solicitud de registro con los datos de ese usuario
     Then el estado de la respuesta debe ser 200
     And el mensaje de respuesta debe ser "Usuario creado"
     And la respuesta debe cumplir con el esquema JSON "SuccesfulOperation.json"

  Scenario: Registro de usuario invalido
    Given un nuevo usuario con detalles invalidos
    When se envía una solicitud de registro con los datos de ese usuario
    Then el estado de la respuesta debe ser 400
    And el mensaje de error debe ser "Este usuario ya existe"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

