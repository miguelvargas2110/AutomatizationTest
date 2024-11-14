Feature: Operaciones CRUD de perfiles a través
  Como usuario de la API
  Quiero gestionar los perfiles de los usuarios
  Para poder crear, actualizar, eliminar y consultar perfiles de manera eficiente

  # Escenarios de creación de perfil
  Scenario: Registro de perfil exitoso
    Given un perfil valido
    When se envía una solicitud POST para crear un perfil
    Then el estado de la respuesta Profiles debe ser 201
    And el mensaje de respuesta Profiles debe ser "Perfil creado exitosamente"
    And la respuesta Profiles debe cumplir con el esquema JSON "SuccesfulOperation.json"

  Scenario: Registro de perfil con "id_usuario" faltante
    Given un perfil invalido
    When se envía una solicitud POST para crear un perfil
    Then el estado de la respuesta Profiles debe ser 400
    And el mensaje de error de respuesta Profiles debe ser "id_usuario es requerido"
    And la respuesta Profiles debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

  # Escenarios de obtención de perfiles
  Scenario: Obtención de todos los perfiles exitosamente
    Given existen perfiles registrados en el servicio de perfiles
    When se envía una solicitud GET para obtener todos los perfiles
    Then el estado de la respuesta Profiles debe ser 200
    And la respuesta debe contener una lista de perfiles

  # Escenarios de actualización de perfil
  Scenario: Actualización de perfil exitoso
    Given un perfil existente
    And los datos de perfil son válidos
    When se envía una solicitud PUT para actualizar el perfil
    Then el estado de la respuesta Profiles debe ser 200
    And el mensaje de respuesta Profiles debe ser "Perfil actualizado exitosamente"
    And la respuesta Profiles debe cumplir con el esquema JSON "SuccesfulOperation.json"

  Scenario: Intento de actualizar un perfil con datos inválidos
    Given un perfil existente
    And los datos de perfil son inválidos
    When se envía una solicitud PUT para actualizar el perfil
    Then el estado de la respuesta Profiles debe ser 400
    And el mensaje de error de respuesta Profiles debe ser "Error al actualizar el perfil"
    And la respuesta Profiles debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

  # Escenarios de eliminación de perfil
  Scenario: Eliminación de perfil exitoso
    Given un perfil existente
    When se envía una solicitud DELETE para eliminar el perfil
    Then el estado de la respuesta Profiles debe ser 200
    And el mensaje de respuesta Profiles debe ser "Perfil eliminado exitosamente"
    And la respuesta Profiles debe cumplir con el esquema JSON "SuccesfulOperation.json"

  Scenario: Intento de eliminar un perfil no existente
    Given un perfil inexistente
    When se envía una solicitud DELETE para eliminar el perfil
    Then el estado de la respuesta Profiles debe ser 404
    And el mensaje de error de respuesta Profiles debe ser "Perfil no encontrado"
    And la respuesta Profiles debe cumplir con el esquema JSON "UnsuccesfulOperation.json"
