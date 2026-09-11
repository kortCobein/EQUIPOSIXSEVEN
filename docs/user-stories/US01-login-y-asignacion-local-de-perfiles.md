# US01 — Login y asignación local de perfiles

**Épica 1:** Autenticación  
**Formato:** Historia de usuario en Markdown para consulta directa en GitHub.

## Historia de usuario

- **Como:** Usuario de la aplicación móvil.
- **Quiero:** Iniciar sesión consumiendo el endpoint /auth/login y descargar mi información de usuario.
- **Para:** Acceder a la aplicación con el rol correspondiente (Administrador, Cliente o Auditor) y ver la interfaz adecuada.

## Criterios de aceptación

### Escenario 1: Autenticación exitosa y mapeo de rol.

- **Dado que:** El usuario llena el formulario con credenciales válidas
- **Cuando:** La API devuelve el token de acceso con código 200 OK.
- **Entonces:** La aplicación decodifica o mapea el ID del usuario, le asigna su rol predefinido, guarda la sesión y navega a la pantalla principal.

### Escenario 2: Credenciales incorrectas.

- **Dado:** Que el usuario ingresa a la vista de login
- **Cuando:** Introduce un nombre de usuario o contraseña que no existen en la base de Fake Store API.
- **Entonces:** La aplicación captura el error 401 (o similar) y muestra una alerta roja indicando "Usuario o contraseña inválidos".

### Escenario 3: Manejo de conectividad de red

- **Dado:** Que el dispositivo móvil se encuentra sin acceso a internet (modo avión o sin señal).
- **Cuando:** El usuario presiona el botón de "Iniciar Sesión".
- **Entonces:** La aplicación detiene la ejecución antes de consumir la API y alerta al usuario sobre la falta de conexión.

## Notas adicionales / Reglas de negocio

- El mapeo de roles debe programarse en el código base, asignando a los IDs 1 y 2 el rol de Administrador, el ID 3 para Auditor, y los IDs restantes como Clientes. El token debe persistirse usando las herramientas de almacenamiento seguro nativas del dispositivo.

---
*Fuente: documento original `US01 - Login y asignaci#U00f3n local de perfiles - copia.docx` del paquete de User Stories.*
