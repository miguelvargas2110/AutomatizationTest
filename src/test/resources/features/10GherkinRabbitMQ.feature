  Feature: Verificación de mensajes en colas de RabbitMQ
  Como desarrollador
  Quiero verificar que los mensajes se envíen y reciban correctamente por medio de RabbitMQ en un entorno de contenedor Docker
  Para asegurarme de que las colas funcionan según lo esperado

  Background:
    Given un contenedor de RabbitMQ está corriendo en Docker
    And las colas "queue1" y "queue2" están disponibles en RabbitMQ

  Scenario: Envío y recepción de mensajes en queue1
    Given quiero enviar un mensaje a la cola "queue1"
    When envío el mensaje "Hola, queue1" a la cola "queue1"
    Then debería recibir el mensaje "Hola, queue1" desde la cola "queue1"

  Scenario: Envío y recepción de mensajes en queue2
    Given quiero enviar un mensaje a la cola "queue2"
    When envío el mensaje "Hola, queue2" a la cola "queue2"
    Then debería recibir el mensaje "Hola, queue2" desde la cola "queue2"
