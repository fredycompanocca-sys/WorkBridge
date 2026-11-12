# 🚀 WorkBridge - Gestión de Microtrabajos y Servicios Estudiantiles

> **Proyecto Final de Programación en Móviles - Ciclo IV (TECSUP)**  
> Aplicación móvil nativa en **Android con Kotlin** y **Jetpack Compose**, desarrollada bajo los principios de **Clean Architecture** y el patrón **MVVM**.

---

## 📱 Descripción del Proyecto

**WorkBridge** es una plataforma móvil diseñada para conectar el talento de estudiantes universitarios mediante la publicación y postulación a microtrabajos y servicios independientes dentro del campus (tutorías académicas, soporte de redes y TI, diseño gráfico, redacción de informes técnicos, logística de eventos, etc.).

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Propósito |
| :--- | :--- | :--- |
| **Lenguaje** | **Kotlin 1.9+** | Lenguaje oficial con Coroutines y StateFlow |
| **Interfaz de Usuario** | **Jetpack Compose + Material 3** | UI declarativa moderna y reactiva |
| **Arquitectura** | **Clean Architecture + MVVM** | Separación en capas: `data`, `domain` y `ui` |
| **Backend & Autenticación** | **Firebase Auth & Cloud Firestore** | Gestión de usuarios y persistencia NoSQL en tiempo real |
| **API Externa / REST** | **Retrofit 2 + Gson + OkHttp** | Consumo del catálogo de categorías académicas |
| **Persistencia Local** | **Room Database (SQLite)** | Almacenamiento local de microtrabajos favoritos (Offline-First) |
| **Multimedia** | **Coil & Compresión Base64** | Procesamiento eficiente de imágenes tomadas con la cámara |
| **Hardware** | **FusedLocationProviderClient** | Geolocalización GPS del campus para publicaciones |

---

## 🏗️ Arquitectura del Proyecto

El código está estrictamente organizado bajo el paquete `com.tecsup.workbridge`:

```text
com.tecsup.workbridge/
├── data/                      # 🗄️ CAPA DE ACCESO A DATOS
│   ├── local/                 # Persistencia SQLite local (Room)
│   │   ├── dao/               # TrabajoDao.kt
│   │   ├── entity/            # TrabajoEntity.kt
│   │   └── AppDatabase.kt     # Singleton de Room
│   ├── remote/                # Conexiones remotas
│   │   ├── api/               # RetrofitClient.kt & WorkBridgeApiService.kt
│   │   └── firebase/          # FirebaseAuthService.kt & FirestoreService.kt
│   └── repository/            # Patrón Repository (Single Source of Truth)
│       ├── AuthRepository.kt
│       └── TrabajoRepository.kt
│
├── domain/                    # 🧠 LÓGICA DE NEGOCIO Y MODELOS PUROS
│   └── model/                 # Trabajo.kt, Usuario.kt, Categoria.kt
│
└── ui/                        # 🎨 CAPA DE PRESENTACIÓN (Jetpack Compose + MVVM)
    ├── components/            # AppTopBar, TrabajoCard, AppAsyncImage, PrimaryButton
    ├── navigation/            # NavHost y rutas selladas en Screen.kt
    ├── theme/                 # Paleta Material 3 (Color.kt, Theme.kt, Type.kt)
    └── screens/               # Pantallas modulares con su respectivo ViewModel
        ├── auth/              # LoginScreen & RegisterScreen
        ├── home/              # HomeScreen (Feed con filtros reactivos)
        ├── detail/            # DetailScreen (Detalle y postulación)
        ├── create/            # CreateJobScreen (Formulario con cámara y GPS)
        └── profile/           # ProfileScreen (Perfil y configuración)
```

---

## ⚡ Flujo de Ejecución y Pruebas

1. **Autenticación:** Registro de estudiante con nombre, correo institucional, carrera y teléfono de contacto.
2. **Catálogo REST:** Carga dinámica de categorías desde un servicio REST externo con Retrofit.
3. **Publicación:** Registro de nuevo trabajo adjuntando evidencia fotográfica y coordenadas GPS actuales.
4. **Modo Offline:** Marcado de microtrabajos como favoritos con soporte de lectura sin conexión a internet mediante Room.
5. **Postulación:** Vista en detalle con enlace directo para contactar al solicitante por correo o WhatsApp.

---

## 🚀 Requisitos e Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/TU_USUARIO/workbridge-android.git
   ```
2. Abrir la carpeta del proyecto en **Android Studio** (Koala / Ladybug o superior).
3. Asegurarse de tener seleccionado **Embedded JDK (jbr-17)** en:  
   `File > Settings > Build Tools > Gradle > Gradle JDK`.
4. Colocar el archivo `google-services.json` dentro de la carpeta `app/`.
5. Ejecutar **Sync Project with Gradle Files**.
6. Conectar un dispositivo físico o emulador con Android 8.0+ (API 26+) y presionar **Run 'app'**.
