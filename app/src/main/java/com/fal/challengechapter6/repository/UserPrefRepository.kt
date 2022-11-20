package com.fal.challengechapter6.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.fal.challengechapter6.UserProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException

//Create DataStore Proto
    private val Context.userPreferencesStore: DataStore<UserProto> by dataStore(
        fileName = "userData",
        serializer = UserPrefSerialize
    )
class UserPrefRepository (private val context: Context) {

    //Save DataStore Proto
    suspend fun saveDataProto(nama : String, id : String){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setNama(nama).build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().setUserId(id).build()
            //preferences.toBuilder()
        }
    }
    //Delete DataStore Proto
    suspend fun deleteData(){
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearNama().build()
        }
        context.userPreferencesStore.updateData { preferences ->
            preferences.toBuilder().clearUserId().build()
        }
    }

    //Read DataStore Proto
    val readProto: Flow<UserProto> = context.userPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("tag", "Error reading sort order preferences.", exception)
                emit(UserProto.getDefaultInstance())
            } else {
                throw exception
            }
        }

}