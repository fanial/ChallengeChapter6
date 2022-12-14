@file:Suppress("BlockingMethodInNonBlockingContext", "BlockingMethodInNonBlockingContext")

package com.fal.challengechapter6.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.fal.challengechapter6.UserProto
import com.fal.challengechapter6.UserProto.parseFrom
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserPrefSerialize : Serializer<UserProto>{
    override val defaultValue: UserProto
        get() = UserProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProto {
        try {
            return parseFrom(input)
        }catch (exception: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: UserProto, output: OutputStream) = t.writeTo(output)
}