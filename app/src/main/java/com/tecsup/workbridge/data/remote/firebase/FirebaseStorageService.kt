package com.tecsup.workbridge.data.remote.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

/**
 * Servicio para gestión de imágenes en WorkBridge.
 *
 * NOTA ARQUITECTÓNICA PARA SUSTENTACIÓN:
 * Dado que Google Cloud ahora requiere una tarjeta de crédito para crear buckets de Firebase Storage,
 * este servicio implementa una estrategia dual:
 * 1. MODO GRATUITO (Base64 Comprimido en Firestore): Redimensiona la foto a 600px y comprime a JPEG ~40KB,
 *    almacenándola dentro del documento de Firestore (Límite: 1MB por documento). 100% GRATIS sin tarjeta.
 * 2. MODO CLOUD STORAGE: Si se activa el plan Blaze de Firebase, sube la imagen al bucket tradicional.
 */
class FirebaseStorageService(
    private val storage: FirebaseStorage? = try { FirebaseStorage.getInstance() } catch (e: Exception) { null }
) {

    /**
     * Procesa la imagen del microtrabajo.
     * Por defecto, utiliza compresión local a Base64 para garantizar que el proyecto funcione
     * en el Plan Gratuito (Spark) de Firebase sin requerir tarjeta de crédito ni facturación.
     */
    suspend fun procesarImagen(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Intentamos la compresión ultra-eficiente en Base64 (Garantiza cero costos)
            val base64DataUrl = comprimirABase64(context, uri)
            if (base64DataUrl != null) {
                return@withContext Result.success(base64DataUrl)
            }

            // Si por alguna razón falla la compresión local y el Storage de Firebase está disponible:
            if (storage != null) {
                val nombreArchivo = "${UUID.randomUUID()}.jpg"
                val referencia = storage.reference.child("trabajos/$nombreArchivo")
                referencia.putFile(uri).await()
                val downloadUrl = referencia.downloadUrl.await().toString()
                return@withContext Result.success(downloadUrl)
            }

            Result.failure(Exception("No se pudo procesar la imagen seleccionada."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Redimensiona y comprime la imagen a JPEG de calidad optimizada (~40 KB),
     * retornando un Data URI que puede ser guardado directamente en Firestore y renderizado por Coil/Compose.
     */
    private fun comprimirABase64(context: Context, uri: Uri, maxDimension: Int = 600, calidad: Int = 65): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return null

            val anchoOriginal = originalBitmap.width
            val altoOriginal = originalBitmap.height
            val ratio = anchoOriginal.toFloat() / altoOriginal.toFloat()

            val nuevoAncho: Int
            val nuevoAlto: Int
            if (anchoOriginal > altoOriginal) {
                nuevoAncho = minOf(anchoOriginal, maxDimension)
                nuevoAlto = (nuevoAncho / ratio).toInt()
            } else {
                nuevoAlto = minOf(altoOriginal, maxDimension)
                nuevoAncho = (nuevoAlto * ratio).toInt()
            }

            val bitmapEscalado = Bitmap.createScaledBitmap(originalBitmap, nuevoAncho, nuevoAlto, true)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmapEscalado.compress(Bitmap.CompressFormat.JPEG, calidad, byteArrayOutputStream)
            val bytes = byteArrayOutputStream.toByteArray()

            "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
}
