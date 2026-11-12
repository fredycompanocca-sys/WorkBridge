package com.tecsup.workbridge.ui.screens.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.tecsup.workbridge.ui.components.AppTopBar
import com.tecsup.workbridge.ui.components.CustomTextField
import com.tecsup.workbridge.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    viewModel: CreateJobViewModel,
    onPublicacionExitosa: () -> Unit,
    onRegresar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Tutorías Académicas") }
    var menuExpandido by remember { mutableStateOf(false) }
    var pagoStr by remember { mutableStateOf("") }
    var campus by remember { mutableStateOf("Campus TECSUP Santa Anita") }
    var latitud by remember { mutableStateOf<Double?>(null) }
    var longitud by remember { mutableStateOf<Double?>(null) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // Selector de Fotos desde Galería o Cámara
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    // Cliente GPS FusedLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Launcher de Permiso de Ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        latitud = loc.latitude
                        longitud = loc.longitude
                        campus = "Ubicación GPS: (Lat: ${"%.4f".format(loc.latitude)}, Lon: ${"%.4f".format(loc.longitude)})"
                    }
                }
            } catch (e: SecurityException) {
                // Manejo de seguridad
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Publicar Microtrabajo",
                puedeRegresar = true,
                onRegresarClick = onRegresar
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // Título
            CustomTextField(
                valor = titulo,
                onValorCambio = { titulo = it },
                etiqueta = "Título del microtrabajo",
                iconoInicio = Icons.Default.Title
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Selector de Categoría (Menú desplegable)
            ExposedDropdownMenuBox(
                expanded = menuExpandido,
                onExpandedChange = { menuExpandido = !menuExpandido },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpandido) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = menuExpandido,
                    onDismissRequest = { menuExpandido = false }
                ) {
                    val listaCats = if (uiState.categorias.isNotEmpty()) {
                        uiState.categorias.map { it.nombre }
                    } else {
                        listOf("Tutorías Académicas", "Soporte TI y Redes", "Diseño & Multimedia", "Redacción", "Logística")
                    }

                    listaCats.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoriaSeleccionada = opcion
                                menuExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pago en Soles
            CustomTextField(
                valor = pagoStr,
                onValorCambio = { pagoStr = it },
                etiqueta = "Presupuesto / Pago Ofrecido (S/.)",
                iconoInicio = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Descripción
            CustomTextField(
                valor = descripcion,
                onValorCambio = { descripcion = it },
                etiqueta = "Detalla lo que necesitas...",
                iconoInicio = Icons.Default.Description,
                lineasMaximas = 4
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Ubicación Campus
            CustomTextField(
                valor = campus,
                onValorCambio = { campus = it },
                etiqueta = "Campus o Lugar de Encuentro",
                iconoInicio = Icons.Default.LocationOn
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón Obtener GPS
            OutlinedButton(
                onClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                                if (loc != null) {
                                    latitud = loc.latitude
                                    longitud = loc.longitude
                                    campus = "GPS: Lat ${"%.4f".format(loc.latitude)}, Lon ${"%.4f".format(loc.longitude)}"
                                }
                            }
                        } catch (e: SecurityException) {
                            // Ignorar
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.MyLocation, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adjuntar mi Ubicación GPS Actual")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Adjuntar Imagen (Cámara / Galería)
            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (imagenUri == null) "Adjuntar Foto / Evidencia" else "Cambiar Foto Seleccionada")
            }

            // Vista previa de imagen seleccionada
            if (imagenUri != null) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = imagenUri,
                    contentDescription = "Foto preliminar",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Error Feedback
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Publicar
            PrimaryButton(
                texto = "Publicar Trabajo",
                onClick = {
                    viewModel.publicarTrabajo(
                        titulo = titulo,
                        descripcion = descripcion,
                        categoria = categoriaSeleccionada,
                        pagoStr = pagoStr,
                        campus = campus,
                        latitud = latitud,
                        longitud = longitud,
                        imagenUri = imagenUri,
                        onExito = onPublicacionExitosa
                    )
                },
                estaCargando = uiState.estaGuardando
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
