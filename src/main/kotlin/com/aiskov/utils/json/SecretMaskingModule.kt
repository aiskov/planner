package com.aiskov.utils.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import org.slf4j.LoggerFactory
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.PROPERTY
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER

/**
 * Marks a field/property as secret. When present Jackson will serialize a masked value (***).
 */
@Retention(RUNTIME)
@Target(PROPERTY, FIELD, VALUE_PARAMETER)
annotation class Secret

class SecretMaskingModule : SimpleModule("SecretMaskingModule") {
    init {
        this.setSerializerModifier(object : BeanSerializerModifier() {
            override fun changeProperties(config: SerializationConfig?, beanDesc: BeanDescription?, beanProperties: MutableList<BeanPropertyWriter>): MutableList<BeanPropertyWriter> {
                val result = super.changeProperties(config, beanDesc, beanProperties)
                for (i in result.indices) {
                    val writer = result[i]
                    val annotated = writer.member?.getAnnotation(Secret::class.java) != null

                    if (annotated) {
                        result[i] = MaskingWriter(writer)
                    }
                }
                return result
            }
        })
    }

    private class MaskingWriter(delegate: BeanPropertyWriter) : BeanPropertyWriter(delegate) {
        override fun serializeAsField(bean: Any?, gen: JsonGenerator?, prov: SerializerProvider?) {
            try {
                if (gen == null) return
                gen.writeFieldName(name)
                gen.writeString("***")
            } catch (ex: Exception) {
                LoggerFactory.getLogger(SecretMaskingModule::class.java).warn("Failed to mask secret field '$name'", ex)
                super.serializeAsField(bean, gen, prov)
            }
        }
    }
}
