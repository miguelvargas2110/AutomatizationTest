Feature: Operaciones Update de Usuarios
  Como usuario de la API
  Quiero gestionar usuarios (crear, leer, actualizar, eliminar) And realizar autenticación
  Para poder mantener los datos de los usuarios And asegurar el acceso

  Scenario: Actualización exitosa de usuario
    Given Un usuario autenticado
    And intenta actualizarse a si mismo
    When se envía una solicitud de actualizacion con los datos
    Then el estado de la respuesta debe ser 200
    And el mensaje de respuesta debe ser "Usuario actualizado"
    And la respuesta debe cumplir con el esquema JSON "SuccesfulOperation.json"


  Scenario: Fallo al actualizar un usuario
    Given Un usuario autenticado
    And intenta actualizar otro usuario
    When se envía una solicitud de actualizacion con los datos
    Then el estado de la respuesta debe ser 401
    And el mensaje de respuesta debe ser "Error: Usuario no autorizado"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"


  Scenario: Fallo al actualizar un usuario existente por datos nulos
    Given Un usuario autenticado
    And intenta actualizarse a si mismo
    When se envía una solicitud de actualizacion sin los datos
    Then el estado de la respuesta debe ser 400
    And el mensaje de error debe ser "Ambos datos a actualizar son nulos"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"


  Scenario: Fallo al actualizar un usuario existente por falta de permisos
    Given Un usuario no autenticado
    And intenta actualizar otro usuario
    When se envía una solicitud de actualizacion con los datos
    Then el estado de la respuesta debe ser 401
    And el mensaje de error debe ser "Error: Usuario no autorizado"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"
