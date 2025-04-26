# Consulter

Consulter es una aplicación de escritorio **a medida** desarrollada específicamente para la gestión de pacientes y documentos médicos en un consultorio. Es una solución **offline**, diseñada para funcionar localmente sin necesidad de conexión a internet, proporcionando un sistema sencillo y eficaz para manejar historias clínicas, notas de evolución y datos de pacientes de manera organizada.

## Características Principales

* **Gestión de Pacientes:** Añade, edita y visualiza la información detallada de tus pacientes.
* **Historias Clínicas:** Crea y consulta historias clínicas completas, incluyendo antecedentes heredofamiliares, no patológicos, patológicos, ginecológicos y la entrevista del paciente.
* **Notas de Evolución:** Registra y gestiona las notas de seguimiento de cada paciente.
* **Exportación de Documentos:** Genera documentos (Historias Clínicas y Notas de Evolución) en formatos PDF y DOCX para su impresión o archivo digital, guardándolos localmente.
* **Sistema de Usuarios Simple:** Diseñado para ser utilizado en un entorno local y privado, no requiere un sistema de seguridad complejo. Incluye la gestión de **dos usuarios preconfigurados**.
* **Base de Datos Local:** Persistencia de datos utilizando JPA con una base de datos relacional (configurada por defecto para MySQL, pensada para una instalación local).
* **Interfaz Intuitiva:** Interfaz de usuario construida con JavaFX y componentes de MaterialFX, buscando ser fácil de usar en el día a día del consultorio.

## Tecnologías Utilizadas

* **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate
* **Frontend:** JavaFX, MaterialFX
* **Base de Datos:** MySQL (configuración para instalación local)
* **Generación de Documentos:** Apache PDFBox (para PDF), Apache POI (para DOCX)
* **Herramientas de Build:** Maven (basado en la estructura `pom.xml` esperada)

## Cómo Empezar

*(Nota: Estas instrucciones asumen una instalación local para el consultorio.)*

### Prerrequisitos

* Java Development Kit (JDK) 17 o superior
* Maven
* Servidor de base de datos MySQL instalado localmente.
* Crear la base de datos `martinez_database` (o modificar `application.properties` si usas otro nombre) y asegurarse de que el usuario y contraseña (`root`/`root` por defecto) sean correctos, o actualizarlos en `src/main/resources/application.properties`. El esquema de la base de datos (`DDL`) debería generarse automáticamente al iniciar la aplicación por primera vez, gracias a `spring.jpa.hibernate.ddl-auto=update`.

### Instalación y Ejecución

1.  Obtén el código fuente. Si está en un repositorio privado, deberás descargarlo o clonarlo usando tus credenciales:
    ```bash
    git clone https://github.com/alann616/consulter # Usa la URL correcta si es privado
    cd consulter
    ```
2.  Configura la base de datos MySQL localmente según los `prerrequisitos`.
3.  Compila el proyecto usando Maven desde la línea de comandos en la raíz del proyecto:
    ```bash
    mvn clean install
    ```
4.  Ejecuta la aplicación. La forma más sencilla es usar el plugin de Spring Boot:
    ```bash
    mvn spring-boot:run
    ```
    Alternativamente, puedes ejecutar el archivo JAR generado (lo encontrarás en la carpeta `target` después de `mvn clean install`):
    ```bash
    java -jar target/consulter-version.jar # Reemplaza 'version' con la versión del build
    ```
5.  La aplicación de escritorio JavaFX se iniciará, presentando la pantalla de login donde podrás seleccionar uno de los usuarios preconfigurados.

## Estructura del Proyecto

El proyecto sigue una estructura estándar para aplicaciones Spring Boot + JavaFX:

* `controller`: Clases JavaFX que manejan la interacción del usuario con la interfaz.
* `model`: Clases que representan los datos y entidades (pacientes, usuarios, documentos).
* `repository`: Interfaces para la comunicación con la base de datos a través de Spring Data JPA.
* `service`: Contiene la lógica de negocio y orquesta las operaciones.
* `util`: Clases auxiliares para tareas como la exportación de documentos.
* `enums`: Definiciones de tipos enumerados (ej: `Gender`, `DocumentType`).
* `components`: Elementos de UI reusables.
* `resources`: Archivos de interfaz visual (`.fxml`), estilos (`.css`) y configuración (`application.properties`).

## Contribución

Este proyecto fue desarrollado **a medida** y no está abierto a contribuciones externas en este momento.

# Consulter

Consulter is a **custom** desktop application developed specifically for managing patients and medical documents in a doctor's office. It is an **offline** solution, designed to function locally without the need for an internet connection, providing a simple and efficient system for handling clinical histories, evolution notes, and patient data in an organized manner.

## Key Features

* **Patient Management:** Add, edit, and view detailed information for your patients.
* **Clinical Histories:** Create and consult comprehensive clinical histories, including hereditary, non-pathological, pathological, and gynecological antecedents, as well as the patient interview.
* **Evolution Notes:** Record and manage follow-up notes for each patient.
* **Document Export:** Generate documents (Clinical Histories and Evolution Notes) in PDF and DOCX formats for printing or digital archiving, saving them locally.
* **Simple User System:** Designed for use in a local, private environment, it does not require a complex security system. It includes management for **two pre-configured users**.
* **Local Database:** Data persistence using JPA with a relational database (configured by default for MySQL, intended for local installation).
* **Intuitive Interface:** User interface built with JavaFX and MaterialFX components, aiming to be easy to use in the daily workflow of the practice.

## Technologies Used

* **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate
* **Frontend:** JavaFX, MaterialFX
* **Database:** MySQL (configuration for local installation)
* **Document Generation:** Apache PDFBox (for PDF), Apache POI (for DOCX)
* **Build Tools:** Maven (based on the expected `pom.xml` structure)

## Getting Started

*(Note: These instructions assume a local installation for the doctor's office.)*

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* Maven
* A local MySQL database server installed.
* Create the database `martinez_database` (or modify `application.properties` if you use a different name) and ensure the user and password (`root`/`root` by default) are correct, or update them in `src/main/resources/application.properties`. The database schema (DDL) should be automatically generated upon first startup thanks to `spring.jpa.hibernate.ddl-auto=update`, but you might need to manually create the database initially.

### Installation and Execution

1.  Obtain the source code. If it's in a private repository, you'll need to download or clone it using your credentials:
    ```bash
    git clone https://github.com/alann616/consulter # Use the correct URL if private
    cd consulter
    ```
2.  Configure the local MySQL database according to the `prerequisites`.
3.  Compile the project using Maven from the command line in the project root:
    ```bash
    mvn clean install
    ```
4.  Run the Spring Boot application. The simplest way is to use the Spring Boot plugin:
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can execute the generated JAR file (you'll find it in the `target` folder after `mvn clean install`):
    ```bash
    java -jar target/consulter-version.jar # Replace 'version' with your build version
    ```
5.  The JavaFX desktop application should launch, showing the login screen where you can select one of the pre-configured users.

## Project Structure

The project follows a typical structure for a Spring Boot + JavaFX application:

* `controller`: JavaFX classes that handle user interaction with the interface.
* `model`: Classes representing data entities (patients, users, documents).
* `repository`: Spring Data JPA interfaces for database communication.
* `service`: Contains the business logic and or orchestrates operations.
* `util`: Utility classes for tasks like document export.
* `enums`: Enumeration definitions (e.g., `Gender`, `DocumentType`).
* `components`: Reusable UI elements and their controllers.
* `resources`: Visual interface files (`.fxml`), styles (`.css`), and configuration (`application.properties`).

## Contribution

This project was developed as a **custom solution** and is not open to external contributions at this time.
