Feature: Operaciones Pageable de Usuarios
  Como usuario de la API
  Quiero gestionar usuarios (crear, leer, actualizar, eliminar) And realizar autenticación
  Para poder mantener los datos de los usuarios And asegurar el acceso

  Scenario: Obtención de usuarios paginados con valores válidos
    Given Hay usuarios en la base de datos
    And se dan valores positivos para 2 y 5
    When se envía una solicitud de paginacion con 2 y 5
    Then el estado de la respuesta debe ser 200
    And la respuesta debe contener una lista de usuarios con detalles de paginación
    And la respuesta debe cumplir con el esquema JSON "SuccesfulPagination.json"


  Scenario: Error de obtención de usuarios paginados con valores negativos
    Given Hay usuarios en la base de datos
    And se dan valores negativos para -1 y -2
    When se envía una solicitud de paginacion con -1 y -2
    Then el estado de la respuesta debe ser 400
    And el mensaje de error debe ser "No se pueden dar valores negativos"
    And la respuesta debe cumplir con el esquema JSON "UnsuccesfulOperation.json"

