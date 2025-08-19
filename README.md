# Consulter API - Backend para Gestión Médica

## Resumen del Proyecto

Consulter API es el backend robusto y escalable que diseñé y desarrollé para un sistema de gestión de pacientes en un consultorio médico. El objetivo principal fue digitalizar y centralizar la información de los pacientes y sus documentos clínicos, creando una fuente única de verdad y optimizando el flujo de trabajo del personal médico.

Este proyecto demuestra mi capacidad para construir aplicaciones del lado del servidor desde cero, aplicando principios de arquitectura limpia y mejores prácticas en el desarrollo con Java y Spring Boot.

## Mis Responsabilidades y Logros

* **Arquitectura y Diseño:** Diseñé la arquitectura completa de la API REST, utilizando un enfoque por capas (Controlador, Servicio, Repositorio) para asegurar una clara separación de responsabilidades y facilitar la mantenibilidad.
* **Modelado de Datos:** Creé un esquema de base de datos relacional complejo utilizando JPA/Hibernate, gestionando entidades como `Patient`, `User` y una jerarquía de `Document` (con `ClinicalHistory` y `EvolutionNote`) mediante estrategias de herencia.
* **Desarrollo de API RESTful:** Implementé todos los endpoints necesarios para las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los recursos principales, siguiendo las convenciones REST.
* **Lógica de Negocio:** Desarrollé toda la lógica de negocio en la capa de servicios, asegurando la integridad de los datos y la correcta ejecución de los procesos del sistema.
* **Configuración y Despliegue:** Gestioné la configuración del proyecto con Maven y preparé la aplicación para su despliegue en un entorno local conectado a una base de datos MySQL.

## Arquitectura y Pila Tecnológica

La selección de tecnologías se basó en la robustez, el rendimiento y el amplio ecosistema de la plataforma Java.

* **Lenguaje:** **Java 23**
* **Framework Principal:** **Spring Boot 3.4.1**
    * *Razón:* Elegí Spring Boot por su capacidad para acelerar el desarrollo, su sistema de inyección de dependencias que facilita el desacoplamiento y su ecosistema maduro para construir APIs REST (Spring Web).
* **Acceso a Datos:** **Spring Data JPA & Hibernate**
    * *Razón:* Utilicé Spring Data JPA para abstraer y simplificar la capa de persistencia, reduciendo significativamente el código repetitivo y permitiéndome enfocar en la lógica de negocio. Hibernate como implementación de JPA ofrece un ORM potente y flexible.
* **Base de Datos:** **MySQL**
    * *Razón:* Un sistema de gestión de bases de datos relacionales probado y confiable, ideal para manejar las relaciones estructuradas entre pacientes y sus documentos.
* **Gestión de Dependencias:** **Maven**
    * *Razón:* Estándar de la industria para la gestión del ciclo de vida y las dependencias en proyectos Java.

## Despliegue Local

Para demostrar la funcionalidad del proyecto, se puede ejecutar localmente.

1.  **Prerrequisitos:** JDK 23, MySQL Server.
2.  **Configuración:** Crear una base de datos llamada `martinez_database` y ajustar las credenciales en `application.properties` si es necesario.
3.  **Ejecución:** Ejecutar la clase principal `ConsulterBackend.java` desde un IDE. El servidor se iniciará en `http://localhost:8080`.

---