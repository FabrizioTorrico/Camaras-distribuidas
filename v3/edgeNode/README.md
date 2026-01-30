# Edge Node (Cámara Raspberry Pi)

Este módulo implementa el nodo de borde que captura video desde la cámara de la Raspberry Pi y lo transmite al Servidor Central.

## Requisitos

- Java 21 o superior.
- Maven 3.6+
- Webcam USB o cámara Raspberry Pi habilitada (ej: `libv4l-dev`).

## Instalación en Raspberry Pi

1. Instalar dependencias del sistema:

   ```bash
   sudo apt-get update
   sudo apt-get install openjdk-21-jdk maven libv4l-dev
   ```

2. Compilar el proyecto:
   ```bash
   mvn clean package
   ```
   Esto generará un archivo `edgeNode-1.0-SNAPSHOT.jar` en la carpeta `target/`.

## Configuración y Ejecución

El nodo se configura mediante **argumentos** al ejecutar el comando.

> **NOTA:** Es obligatorio tener una cámara conectada. Si no se detecta cámara, el programa terminará inmediatamente.

### Uso:

```bash
java -jar target/edgeNode-1.0-SNAPSHOT.jar [HOST] [PUERTO] [ID_CAMARA]
```

Donde:

- `[HOST]`: IP o Hostname del Servidor Central (Defecto: `localhost`)
- `[PUERTO]`: Puerto del Servidor Central (Defecto: `5555`)
- `[ID_CAMARA]`: Identificador único de la cámara (Defecto: `cam1`)

### Ejemplos:

1. **Uso por defecto (localhost:5555, cam1):**

   ```bash
   java -jar target/edgeNode-1.0-SNAPSHOT-shaded.jar
   ```

2. **Cambiar solo la IP del servidor (al puerto 5555 y cam1):**

   ```bash
   java -jar target/edgeNode-1.0-SNAPSHOT-shaded.jar 192.168.1.100
   ```

3. **Cambiar todo:**
   ```bash
   java -jar target/edgeNode-1.0-SNAPSHOT-shaded.jar 192.168.1.100 5555 cam2
   ```
